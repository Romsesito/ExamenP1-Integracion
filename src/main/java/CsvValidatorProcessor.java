import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class CsvValidatorProcessor implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        InputStream is = exchange.getIn().getBody(InputStream.class);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        boolean esValido = true;
        String linea;
        int numeroLinea = 0;

        while ((linea = reader.readLine()) != null) {
            numeroLinea++;
            String[] columnas = linea.split(",", -1);

            if (numeroLinea == 1) {
                if (!linea.contains("patient_id") || !linea.contains("full_name")
                        || !linea.contains("appointment_date") || !linea.contains("insurance_code")) {
                    esValido = false;
                    break;
                }
                continue;
            }

            if (columnas.length < 4) {
                esValido = false;
                break;
            }

            for (String col : columnas) {
                if (col.trim().isEmpty()) {
                    esValido = false;
                    break;
                }
            }
            if (!esValido) break;

            String fecha = columnas[2].trim();
            if (!fecha.matches("\\d{4}-\\d{2}-\\d{2}")) {
                esValido = false;
                break;
            }

            String seguro = columnas[3].trim();
            if (!seguro.equals("IESS") && !seguro.equals("PRIVADO") && !seguro.equals("NINGUNO")) {
                esValido = false;
                break;
            }
        }

        exchange.setProperty("esValido", esValido);
        reader.close();
    }
}