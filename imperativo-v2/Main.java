import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

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

            // Soma de "Active" de todos os países em que "Confirmed" é maior ou igual a n1
            int activeSum = 0;
            for (DataEntry entry : dataEntries) {
                if (entry.getConfirmed() >= n1) {
                    activeSum += entry.getActive();
                }
            }

            // n2 países com maiores valores de "Active"
            List<DataEntry> topActiveCountries = new ArrayList<>(dataEntries);
            Collections.sort(topActiveCountries, Comparator.comparingInt(DataEntry::getActive).reversed());
            topActiveCountries = topActiveCountries.subList(0, Math.min(n2, topActiveCountries.size()));

            // "Deaths" dos n3 países com menores valores de "Confirmed" entre os n2 países
            // com maiores valores de "Active"
            List<DataEntry> lowestConfirmedCountries = new ArrayList<>(topActiveCountries);
            Collections.sort(lowestConfirmedCountries, Comparator.comparingInt(DataEntry::getConfirmed));
            int limit = Math.min(n3, Math.min(n2, lowestConfirmedCountries.size()));
            if (limit > topActiveCountries.size()) {
                limit = topActiveCountries.size();
            }
            lowestConfirmedCountries = lowestConfirmedCountries.subList(0, limit);
            List<Integer> deathsList = new ArrayList<>();
            for (DataEntry entry : lowestConfirmedCountries) {
                deathsList.add(entry.getDeaths());
            }

            // n4 países com os maiores valores de "Confirmed" em ordem alfabética
            List<DataEntry> topConfirmedEntries = new ArrayList<>(dataEntries);
            Collections.sort(topConfirmedEntries, Comparator.comparingInt(DataEntry::getConfirmed).reversed());
            topConfirmedEntries = topConfirmedEntries.subList(0, Math.min(n4, topConfirmedEntries.size()));

            List<String> topConfirmedCountries = new ArrayList<>();
            for (DataEntry entry : topConfirmedEntries) {
                topConfirmedCountries.add(entry.getCountry());
            }
            Collections.sort(topConfirmedCountries);

            // Exibindo os resultados
            System.out.println(activeSum);
            deathsList.forEach(System.out::println);
            topConfirmedCountries.forEach(System.out::println);

        } catch (IOException e) {
            System.out.println("Erro ao ler o arquivo: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            System.out.println("Um erro inesperado ocorreu: " + e.getMessage());
        }
    }

    private static List<DataEntry> readDataEntriesFromFile(String fileName) throws IOException {
        List<DataEntry> dataEntries = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Parsing das linhas do arquivo
                String[] parts = line.split(",");
                if (parts.length < 5) {
                    throw new IOException("Dados insuficientes na linha!");
                }
                String country = parts[0];
                int confirmed = Integer.parseInt(parts[1]);
                int deaths = Integer.parseInt(parts[2]);
                int active = Integer.parseInt(parts[4]);
                DataEntry entry = new DataEntry(country, confirmed, deaths, active);
                dataEntries.add(entry);
            }
        }
        return dataEntries;
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
