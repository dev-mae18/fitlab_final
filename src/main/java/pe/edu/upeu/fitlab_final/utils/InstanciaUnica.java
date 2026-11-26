package pe.edu.upeu.fitlab_final.utils;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class InstanciaUnica implements AutoCloseable {
    private final FileChannel canal;
    private final FileLock bloqueo;

    private InstanciaUnica(FileChannel canal, FileLock bloqueo) {
        this.canal = canal;
        this.bloqueo = bloqueo;
    }

    public static InstanciaUnica obtener() throws IOException {
        Path carpeta = Path.of(System.getProperty("user.home"), "FitLab");
        Files.createDirectories(carpeta);
        FileChannel canal = FileChannel.open(carpeta.resolve("fitlab.lock"),
                StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        try {
            FileLock bloqueo = canal.tryLock();
            if (bloqueo == null) {
                canal.close();
                return null;
            }
            return new InstanciaUnica(canal, bloqueo);
        } catch (OverlappingFileLockException e) {
            canal.close();
            return null;
        } catch (IOException | RuntimeException e) {
            try {
                canal.close();
            } catch (IOException cierre) {
                e.addSuppressed(cierre);
            }
            throw e;
        }
    }

    @Override
    public void close() throws IOException {
        if (canal.isOpen()) {
            try {
                if (bloqueo.isValid()) bloqueo.release();
            } finally {
                canal.close();
            }
        }
    }
}
