import io.dropwizard.Application;
import io.dropwizard.java8.Java8Bundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import rest.AccountResource;
import rest.TransactionResource;

public class MoneyTransferApplication extends Application<ServerConfiguration> {

    public static void main(String[] args) throws Exception {
        new MoneyTransferApplication().run(args);
    }

    @Override
    public void run(ServerConfiguration configuration, Environment environment) throws Exception {
        environment.jersey().register(new ApplicationBinder());
        environment.jersey().register(new AccountResource());
        environment.jersey().register(new TransactionResource());

    }

    public void initialize(Bootstrap<ServerConfiguration> bootstrap) {
        bootstrap.addBundle(new Java8Bundle());
    }
}
