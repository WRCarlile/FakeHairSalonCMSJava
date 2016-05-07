import org.sql2o.*;
import org.junit.*;
import static org.junit.Assert.*;
import org.fluentlenium.adapter.FluentTest;
import org.junit.ClassRule;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import static org.assertj.core.api.Assertions.assertThat;
import static org.fluentlenium.core.filter.FilterConstructor.*;

public class AppTest extends FluentTest {
  public WebDriver webDriver = new HtmlUnitDriver();

  @Override
  public WebDriver getDefaultDriver() {
    return webDriver;
  }
  @Before
public void setUp() {
  DB.sql2o = new Sql2o("jdbc:postgresql://localhost:5432/hair_salon_test", null, null);
}

@After
public void tearDown() {
  try(Connection con = DB.sql2o.open()) {
    String deleteClientsQuery = "DELETE FROM clients *;";
    String deleteStylistsQuery = "DELETE FROM stylists *;";
    con.createQuery(deleteClientsQuery).executeUpdate();
    con.createQuery(deleteStylistsQuery).executeUpdate();
  }
}

  @ClassRule
  public static ServerRule server = new ServerRule();

}
