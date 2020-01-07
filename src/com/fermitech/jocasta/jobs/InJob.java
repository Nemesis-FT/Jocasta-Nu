package com.fermitech.jocasta.jobs;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipOutputStream;

public abstract class InJob extends Job {
    protected FileInputStream stream;

    public InJob(String source, String destination) throws FileNotFoundException {
        super(source, destination);
    }

    public InJob(String source, String destination, FileInputStream stream) throws FileNotFoundException {
        super(source, destination);
        this.stream = stream;
    }

    @Override
    protected void writer(OutputStream outputStream, long size) throws IOException {
        byte[] buffer = new byte[(int) size];
        int value = this.stream.read(buffer);
        if (value != -1) {
            outputStream.write(buffer);
        }
    }

    @Override
    public void execute() throws IOException {
        super.execute();
        this.stream = new FileInputStream(this.file);
    }

    @Override
    public String toString() {
        return super.toString() + "Input ";
    }
}
