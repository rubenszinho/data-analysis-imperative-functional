import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class Main {
    public static void main(String[] args) {
        try {
            // Lendo os dados do arquivo
            List<DataEntry> dataEntries = readDataEntriesFromFile("dados.csv");

            // Obtendo os valores de n1, n2, n3 e n4
            Scanner scanner = new Scanner(System.in);
            int n1 = scanner.nextInt();
            int n2 = scanner.nextInt();
            int n3 = scanner.nextInt();
            int n4 = scanner.nextInt();
            scanner.close();

            // Checando se os valores de n1, n2, n3 e n4 são válidos
            if (n1 < 0 || n2 < 0 || n3 < 0 || n4 < 0) {
                throw new IllegalArgumentException("Os números inseridos não são válidos!");
            }

            // Calculando e exibindo os resultados
            calculateAndPrintResults(dataEntries, n1, n2, n3, n4);

        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("Um erro inesperado ocorreu: " + e.getMessage());
        }
    }

    private static void calculateAndPrintResults(List<DataEntry> dataEntries, int n1, int n2, int n3, int n4) {
        // Soma de "Active" de todos os países em que "Confirmed" é maior ou igual a n1
        int activeSum = calculateActiveSum(dataEntries, n1);

        // n2 países com maiores valores de "Active"
        List<DataEntry> topActiveCountries = calculateTopActiveCountries(dataEntries, n2);

        // "Deaths" dos n3 países com menores valores de "Confirmed" entre os n2 países
        // com maiores valores de "Active"
        List<Integer> deathsList = calculateDeathsList(topActiveCountries, n3);

        // n4 países com os maiores valores de "Confirmed" em ordem alfabética
        List<String> topConfirmedCountries = calculateTopConfirmedCountries(dataEntries, n4);

        // Exibindo os resultados
        System.out.println(activeSum);
        deathsList.forEach(System.out::println);
        topConfirmedCountries.forEach(System.out::println);
    }

    private static List<DataEntry> readDataEntriesFromFile(String fileName) throws IOException {
        try (Stream<String> lines = Files.lines(Paths.get(fileName))) {
            return lines
                    .map(line -> line.split(","))
                    .filter(parts -> parts.length >= 5)
                    .map(parts -> new DataEntry(parts[0], Integer.parseInt(parts[1]), Integer.parseInt(parts[2]),
                            Integer.parseInt(parts[4])))
                    .collect(Collectors.toList());
        }
    }

    private static int calculateActiveSum(List<DataEntry> dataEntries, int n1) {
        return dataEntries.stream()
                .filter(entry -> entry.getConfirmed() >= n1)
                .mapToInt(DataEntry::getActive)
                .sum();
    }

    private static List<DataEntry> calculateTopActiveCountries(List<DataEntry> dataEntries, int n2) {
        return dataEntries.stream()
                .sorted(Comparator.comparingInt(DataEntry::getActive).reversed())
                .limit(n2)
                .collect(Collectors.toList());
    }

    private static List<Integer> calculateDeathsList(List<DataEntry> topActiveCountries, int n3) {
        return topActiveCountries.stream()
                .sorted(Comparator.comparingInt(DataEntry::getConfirmed))
                .limit(n3)
                .map(DataEntry::getDeaths)
                .collect(Collectors.toList());
    }

    private static List<String> calculateTopConfirmedCountries(List<DataEntry> dataEntries, int n4) {
        return dataEntries.stream()
                .sorted(Comparator.comparingInt(DataEntry::getConfirmed).reversed())
                .limit(n4)
                .map(DataEntry::getCountry)
                .sorted()
                .collect(Collectors.toList());
    }

    private static class DataEntry {
        private String country;
        private int confirmed;
        private int deaths;
        private int active;

        public DataEntry(String country, int confirmed, int deaths, int active) {
            this.country = country;
            this.confirmed = confirmed;
            this.deaths = deaths;
            this.active = active;
        }

        public String getCountry() {
            return country;
        }

        public int getConfirmed() {
            return confirmed;
        }

        public int getDeaths() {
            return deaths;
        }

        public int getActive() {
            return active;
        }
    }
}
