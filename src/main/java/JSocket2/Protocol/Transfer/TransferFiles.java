package JSocket2.Protocol.Transfer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Manages the physical files associated with a transfer, including a temporary data file
 * and a JSON file for storing transfer state and metadata.
 */
public class TransferFiles {
    private final RandomAccessFile file;
    private final String infoFilePath;
    private final String tmpFilePath;
    private TransferInfo info;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Constructs a TransferFiles object, creating or loading the necessary files.
     *
     * @param tmpFilePath       The path to the temporary data file.
     * @param infoFilePath      The path to the .info metadata file.
     * @param destinationPath   The final destination directory for the file.
     * @param fileId            The unique ID of the transfer.
     * @param fileName          The name of the file.
     * @param fileExtension     The extension of the file.
     * @param totalChunksCount  The total number of chunks.
     * @param fileSize          The total size of the file.
     * @throws IOException If an error occurs creating or accessing files.
     */
    public TransferFiles(String tmpFilePath,String infoFilePath,String destinationPath,String fileId,String fileName,String fileExtension,int totalChunksCount,long fileSize) throws IOException {
        File tmpFile = new File(tmpFilePath);
        File parentDir = tmpFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }
        this.tmpFilePath = tmpFilePath;
        this.file = new RandomAccessFile(tmpFilePath,"rw");
        this.infoFilePath = infoFilePath;
        File mf = new File(infoFilePath);
        if (mf.exists()) {
            readInfoFromDisk(mf);
        } else {
            this.info = new TransferInfo(fileId,fileName,fileExtension,destinationPath, 0, 0,totalChunksCount,fileSize);
            saveinfoToDisk();
        }
    }

    /**
     * Checks if a transfer can be resumed by verifying the existence of its .tmp and .info files.
     *
     * @param fileId The ID of the file transfer.
     * @return True if files exist, false otherwise.
     */
    public static boolean canLoad(String fileId) {
        String systemTempDir = System.getProperty("java.io.tmpdir");
        String basePath = Paths.get(systemTempDir, "JTelegram").toString();
        File tmpFile = new File(basePath, fileId + ".tmp");
        File infoFile = new File(basePath, fileId + ".info");
        return tmpFile.exists() && infoFile.exists();
    }

    /**
     * Loads a paused transfer from its .tmp and .info files in the default temp directory.
     *
     * @param fileId The ID of the transfer to load.
     * @return A new {@link TransferFiles} instance, or null if loading fails.
     */
    public static TransferFiles Load(String fileId) {
        String systemTempDir = System.getProperty("java.io.tmpdir");
        String basePath = Paths.get(systemTempDir, "JTelegram").toString();

        File infoFile = new File(basePath, fileId + ".info");
        File tmpFile = new File(basePath, fileId + ".tmp");

        if (!tmpFile.exists() || !infoFile.exists()) {
            return null;
        }

        try {
            Gson gson = new Gson();
            TransferInfo info;
            try (FileReader reader = new FileReader(infoFile)) {
                info = gson.fromJson(reader, TransferInfo.class);
            }

            return new TransferFiles(
                    tmpFile.getAbsolutePath(),
                    infoFile.getAbsolutePath(),
                    info.getDestinationPath(),
                    fileId,
                    info.getFileName(),
                    info.getFileExtension(),
                    info.getTotalChunksCount(),
                    info.getFileSize()
            );
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Writes a chunk of data to the temporary file at a specific offset.
     *
     * @param offset     The position in the file to write to.
     * @param data       The byte array of data to write.
     * @param chunkIndex The index of the chunk being written.
     * @throws IOException If a file I/O error occurs.
     */
    public synchronized void writeChunk(long offset, byte[] data, int chunkIndex) throws IOException {
        file.seek(offset);
        file.write(data);

        info.setLastWrittenOffset(offset + data.length);
        info.setLastChunkIndex(chunkIndex);

        saveinfoToDisk();
    }

    /**
     * Closes the file handles and updates the transfer state to Paused or Complete.
     *
     * @throws IOException If a file I/O error occurs.
     */
    public synchronized void closeAndCleanup() throws IOException {
        if (file != null) {
            file.close();
        }
        if(isComplete()) {
            info.setTransferState(TransferState.Complete);
        } else {
            info.setTransferState(TransferState.Paused);
        }
        saveinfoToDisk();
    }

    /**
     * Gets the path to the temporary data file.
     * @return The .tmp file path.
     */
    public String getTmpFilePath(){
        return tmpFilePath;
    }

    /**
     * Gets the path to the metadata file.
     * @return The .info file path.
     */
    public String getinfoFilePath() {
        return infoFilePath;
    }

    /**
     * Gets the current transfer information.
     * @return The {@link TransferInfo} object.
     * @throws IOException If the info cannot be read from disk.
     */
    public synchronized TransferInfo getinfo() throws IOException {
        if(info == null){
            readInfoFromDisk();
        }
        return info;
    }

    /**
     * Finalizes the transfer by moving the temporary file to its final destination
     * and deleting the metadata file.
     *
     * @throws IOException If a file I/O error occurs during the move.
     */
    public void finalizeTransfer() throws IOException {
        closeAndCleanup();

        File destinationDir = new File(info.getDestinationPath());
        if (!destinationDir.exists()) {
            destinationDir.mkdirs();
        }
        String baseName = info.getFileName();
        String extension = info.getFileExtension();
        File finalFile = new File(destinationDir, baseName + "." + extension);
        int index = 1;
        while (finalFile.exists()) {
            finalFile = new File(destinationDir, baseName + " (" + index + ")." + extension);
            index++;
        }

        Files.move(Path.of(tmpFilePath), finalFile.toPath());
        new File(infoFilePath).delete();
    }

    /**
     * Saves the current {@link TransferInfo} state to the .info file.
     * @throws IOException If a file I/O error occurs.
     */
    private void saveinfoToDisk() throws IOException {
        try (FileWriter writer = new FileWriter(infoFilePath)) {
            gson.toJson(info, writer);
        }
    }

    /**
     * Reads the {@link TransferInfo} state from the .info file.
     * @throws IOException If a file I/O error occurs.
     */
    private void readInfoFromDisk() throws IOException {
        readInfoFromDisk(new File(infoFilePath));
    }

    /**
     * Reads the {@link TransferInfo} state from a given file.
     * @param file The .info file.
     * @throws IOException If a file I/O error occurs.
     */
    private void readInfoFromDisk(File file) throws IOException {
        try (FileReader reader = new FileReader(file)) {
            this.info = gson.fromJson(reader, TransferInfo.class);
        }
    }

    /**
     * Factory method to create a new set of transfer files in a specified directory.
     *
     * @param fileId             The unique ID for the transfer.
     * @param fileName           The file's name.
     * @param fileExtension      The file's extension.
     * @param destinationPath    The final save directory.
     * @param tempDirectoryPath  The directory for .tmp and .info files.
     * @param totalChunks        The total number of chunks.
     * @param fileSize           The total file size.
     * @return A new {@link TransferFiles} instance.
     * @throws IOException If an error occurs during file creation.
     */
    public static TransferFiles Create(String fileId,String fileName,String fileExtension,String destinationPath, String tempDirectoryPath,int totalChunks,long fileSize) throws IOException {
        String tmpFileName = fileId + ".tmp";
        String infoFileName = fileId + ".info";
        String tmpfilePath = Paths.get(tempDirectoryPath, tmpFileName).toString();
        String infoFilePath = Paths.get(tempDirectoryPath, infoFileName).toString();
        return new TransferFiles(tmpfilePath,infoFilePath,destinationPath,fileId,fileName,fileExtension,totalChunks,fileSize);
    }

    /**
     * Factory method to create a new set of transfer files in the system's default temporary directory.
     *
     * @param fileId           The unique ID for the transfer.
     * @param fileName         The file's name.
     * @param fileExtension    The file's extension.
     * @param destinationPath  The final save directory.
     * @param totalChunks      The total number of chunks.
     * @param fileSize         The total file size.
     * @return A new {@link TransferFiles} instance.
     * @throws IOException If an error occurs during file creation.
     */
    public static TransferFiles CreateDefault(String fileId,String fileName,String fileExtension,String destinationPath,int totalChunks,long fileSize) throws IOException {
        String systemTempDir = System.getProperty("java.io.tmpdir");
        return Create(fileId,fileName,fileExtension,destinationPath,Paths.get(systemTempDir,"JTelegram").toString(),totalChunks,fileSize);
    }

    /**
     * Checks if the transfer is complete.
     *
     * @return True if the last written chunk is the final chunk, false otherwise.
     */
    public boolean isComplete() {
        return info.getLastChunkIndex() == info.getTotalChunksCount()-1;
    }
}