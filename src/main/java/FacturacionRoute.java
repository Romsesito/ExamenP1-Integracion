import org.apache.camel.builder.RouteBuilder;

public class FacturacionRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        from("file:data/input?delete=true")
                .routeId("ProcesoPreRegistros")
                .log("INFO: Archivo detectado en input -> ${file:name}")
                .process(new CsvValidatorProcessor())
                .choice()
                .when(exchangeProperty("esValido").isEqualTo(true))
                .log("SUCCESS: Archivo ${file:name} es VÁLIDO. Copiando a output y archive.")
                .multicast()
                .to("file:data/output")
                .to("file:data/archive?fileName=${file:name.noext}_${date:now:yyyy-MM-dd_HHmmss}.${file:ext}")
                .end()
                .endChoice()
                .otherwise()
                .log("ERROR: Archivo ${file:name} es INVÁLIDO. Movido a error.")
                .to("file:data/error")
                .end();
    }
}