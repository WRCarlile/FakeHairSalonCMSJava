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
  @Test
    public void rootTest() {
      goTo("http://localhost:4567/");
      assertThat(pageSource()).contains("Hair Salon");
    }

  @Test
  public void stylistIsCreatedTest() {
    goTo("http://localhost:4567/");
    fill("#name").with("Paul Mitchel");
    submit("#stylistBtn");
    assertThat(pageSource()).contains("Your stylist has been saved.");
  }

  @Test
  public void stylistIsDisplayedTest() {
    Stylist myStylist = new Stylist("Paul Mitchel");
    myStylist.save();
    String stylistPath = String.format("http://localhost:4567/stylists/%d", myStylist.getId());
    goTo(stylistPath);
    assertThat(pageSource()).contains("Paul Mitchel");
  }


  @Test
  public void stylistShowPageDiplayName() {
    goTo("http://localhost:4567/stylists/new");
    fill("#name").with("Paul Mitchel");
    submit(".btn");
    click("a", withText("View stylists"));
    click("a", withText("Paul Mitchel"));
    assertThat(pageSource()).contains("Paul Mitchel");
  }

  @Test
  public void stylistClientsFormIsDisplayed() {
    goTo("http://localhost:4567/stylists/new");
    fill("#name").with("Paul Mitchel");
    submit(".btn");
    click("a", withText("View stylists"));
    click("a", withText("Paul Mitchel"));
    click("a", withText("Add a new client"));
    assertThat(pageSource()).contains("Add a client to Paul Mitchel");
  }

  @Test
  public void allClientsDisplayNameOnStylistPage() {
    Stylist myStylist = new Stylist ("Paul Mitchel");
    myStylist.save();
    Client firstClient = new Client ("Bob Smith", myStylist.getId());
    firstClient.save();
    Client secondClient = new Client("Ted Smith", myStylist.getId());
    secondClient.save();
    String stylistPath = String.format("http://localhost:4567/stylists/%d", myStylist.getId());
    goTo(stylistPath);
    assertThat(pageSource()).contains("Bob Smith");
    assertThat(pageSource()).contains("Ted Smith");

  }

  @Test
  public void clientShowPage() {
    Stylist myStylist = new Stylist("Paul Mitchel");
    myStylist.save();
    Client myClient = new Client("Bob Smith", myStylist.getId());
    myClient.save();
    String stylistPath = String.format("http://localhost:4567/stylists/%d", myStylist.getId());
    goTo(stylistPath);
    click("a", withText("Bob Smith"));
    assertThat(pageSource()).contains("Bob Smith");
    assertThat(pageSource()).contains("Return to Paul Mitchel");
  }
  @Test
  public void clientUpdate() {
    Stylist myStylist = new Stylist("Paul Mitchel");
    myStylist.save();
    Client myClient = new Client("Bob Smith", myStylist.getId());
    myClient.save();
    String clientPath = String.format("http://localhost:4567/stylists/%d/clients/%d", myStylist.getId(), myClient.getId());
    goTo(clientPath);
    fill("#name").with("Robert Smith");
    submit("#update-client");
    assertThat(pageSource()).contains("Robert Smith");
  }

  @Test
  public void clientDelete() {
    Stylist myStylist = new Stylist("Paul Mitchel");
    myStylist.save();
    Client myClient = new Client("Bob Smith", myStylist.getId());
    myClient.save();
    String clientPath = String.format("http://localhost:4567/stylists/%d/clients/%d", myStylist.getId(), myClient.getId());
    goTo(clientPath);
    submit("#delete-client");
    assertEquals(0, Client.all().size());
  }

}
