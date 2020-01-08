package com.fermitech.jocasta.jobs;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class FixJob extends OutJob {
    private Vector<File> files;
    private long total_size;

    public FixJob(String source, String destination) throws FileNotFoundException {
        super(source, destination, "joca");
        files = null;
        total_size = 0;
    }

    private File[] locateFiles() {
        Vector<File> correct = new Vector<File>();
        File folder = new File((file.getAbsoluteFile().getParent()));
        File[] all_files = folder.listFiles();
        for (File tmp : all_files) {
            String[] companion_components = tmp.getName().split("\\.");
            if (name_components[0].equals(companion_components[0]) && name_components.length == companion_components.length) {
                boolean flag = true;
                for (int i = 1; i < name_components.length - 1; i++) {
                    if (!name_components[i].equals(companion_components[i])) {
                        flag = false;
                    }
                }
                if (flag) {
                    correct.add(tmp);
                    total_size = total_size+tmp.length();
                }
            }
        }
        File[] result = new File[correct.size()];
        for (File tmp:correct){
            String[] companion_components = tmp.getName().split("\\.");
            result[Integer.parseInt(companion_components[companion_components.length-1])-1]=tmp;
        }
        return result;
    }



    private File[] sortFiles(){
        Object[] array = this.files.toArray();
        File[] tmp = Arrays.copyOf(array, array.length, File[].class);
        Arrays.sort(tmp);
        return tmp;
    }

    @Override
    public void execute() throws IOException {
        super.execute();
        stream.close();
        File[] filearray = locateFiles();
        long leftover = total_size%filearray.length;
        long test = leftover;
        int counter = 0;
        for (File current : filearray) {
            FileInputStream current_stream = new FileInputStream(current); //magari la metto dentro al suo posto nella struttura dati tanto per
            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(destination + "/" + nextFileNameGenerator(), true));
            bufferControl(current.length(), outputStream, current_stream);
            //if(leftover > 0){ utter trash that needs to be removed
            //    bufferControl(file.length(), outputStream, current_stream);
            //    writer(outputStream, leftover, current_stream);
            //    leftover = 0;
            //}
            //else{
            //    bufferControl(file.length()-test, outputStream, current_stream);
            //    test = 0;
            //}
            outputStream.close();
            current_stream.close();
            if(counter==filearray.length && leftover>0){
                bufferControl(file.length(), outputStream, current_stream);
                writer(outputStream, leftover, current_stream);
            }
            counter++;
        }
    }

    protected void bufferControl(long value, OutputStream outputStream, FileInputStream file) throws IOException {
        if(value > max_size){
            long n_reads = value/max_size;
            long leftover_reads = value%max_size;
            for(int j=0; j<n_reads; j++){
                writer(outputStream, max_size, file);
            }
            if(leftover_reads>0){
                writer(outputStream, leftover_reads, file);
            }
        }
        else{
            writer(outputStream, value, file);
        }
    }

    protected void writer(OutputStream outputStream, long size, FileInputStream file) throws IOException {
        byte[] buffer = new byte[(int) size];
        int value = file.read(buffer);
        if (value != -1) {
            outputStream.write(buffer);
        }
    }

    @Override
    public String toString() {
        return super.toString() + " Fix";
    }
}
