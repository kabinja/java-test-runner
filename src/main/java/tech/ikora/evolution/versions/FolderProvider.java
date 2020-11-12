package tech.ikora.evolution.versions;

import tech.ikora.evolution.configuration.FolderConfiguration;
import tech.ikora.evolution.configuration.ProcessConfiguration;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FolderProvider implements VersionProvider {
    private final File rootFolder;
    private final FolderConfiguration.NameFormat nameFormat;
    private final String dateFormat;
    private final ProcessConfiguration processConfiguration;

    public FolderProvider(File rootFolder, FolderConfiguration.NameFormat nameFormat, String dateFormat, ProcessConfiguration processConfiguration) {
        this.rootFolder = rootFolder;
        this.nameFormat = nameFormat;
        this.dateFormat = dateFormat;
        this.processConfiguration = processConfiguration;
    }

    @Override
    public void clean() {
        //nothing to do, the folders are read only.
    }

    @Override
    public Iterator<Version> iterator() {
        return new Iterator<>() {
            private final List<File> subFolders = getSubFolders();
            private final Iterator<File> subFoldersIterator = subFolders.iterator();
            private int commitCounter = 0;

            @Override
            public boolean hasNext() {
                return subFoldersIterator.hasNext();
            }

            @Override
            public Version next() {
                final File subFolder = subFoldersIterator.next();
                LocalDateTime date;
                String commitId;
                switch (nameFormat){
                    case DATE:
                        date = LocalDateTime.parse(subFolder.getName(), DateTimeFormatter.ofPattern(dateFormat));
                        commitId = String.format("Commit%d", ++commitCounter);
                        break;

                    case VERSION:
                        date = LocalDateTime.now();
                        commitId = subFolder.getName();
                        break;

                    default: throw new RuntimeException("nameFormat needs to be either DATE or VERSION");
                }

                return new Version(
                        subFolder.getName(),
                        subFolder,
                        date,
                        commitId,
                        "",
                        processConfiguration);
            }

            private List<File> getSubFolders(){
                return Stream.of(Objects.requireNonNull(rootFolder.listFiles(
                            (File current, String name) -> new File(current, name).isDirectory())
                        ))
                        .sorted(this::sortSubFolder)
                        .collect(Collectors.toList());
            }

            private int sortSubFolder(File file1, File file2) {
                final String name1 = file1.getName();
                final String name2 = file2.getName();

                int compare = 0;

                switch (nameFormat){
                    case VERSION:
                        compare = name1.compareToIgnoreCase(name2);
                        break;
                    case DATE:
                        final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(dateFormat);
                        final LocalDate date1 = LocalDate.from(dateTimeFormatter.parse(name1));
                        final LocalDate date2 = LocalDate.from(dateTimeFormatter.parse(name2));
                        compare = date1.compareTo(date2);
                        break;
                }

                return compare;
            }
        };
    }
}
