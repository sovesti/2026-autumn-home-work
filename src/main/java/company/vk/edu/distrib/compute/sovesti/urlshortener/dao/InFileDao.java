package company.vk.edu.distrib.compute.sovesti.urlshortener.dao;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.NoSuchElementException;
import java.util.Objects;

import company.vk.edu.distrib.compute.Dao;

public final class InFileDao implements Dao<String> {

    private final File file;
    private final PrintWriter write;
    private final DaoOperations operations;
    private final Dao<String> memory;

    public InFileDao(File file) throws IOException {
        this.file = Objects.requireNonNull(file);
        write = new PrintWriter(new FileWriter(file, true), true);
        operations = new DaoOperations();
        memory = new InMemoryDao<>();
    }

    public InFileDao(String path) throws IOException {
        this(new File(path));
    }

    public void read() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            operations.fill(reader.lines());
        }
        operations.execute(memory);
    }

    @Override
    public void close() throws IOException {
        write.close();
        memory.close();
    }

    @Override
    public String get(String key) throws NoSuchElementException, IOException {
        return memory.get(key);
    }

    @Override
    public void upsert(String key, String value) throws IOException {
        addOperation(new DaoOperation.Upsert(key, value));
    }

    @Override
    public void delete(String key) throws IOException {
        addOperation(new DaoOperation.Delete(key));
    }

    private void addOperation(DaoOperation op) throws IOException {
        operations.add(op);
        op.execute(memory);
        write.println(new KeyValuePair(op.label(), op.serialized()).raw());
    }
}
