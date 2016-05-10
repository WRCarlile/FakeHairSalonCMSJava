import org.sql2o.*;
import org.junit.*;
import static org.junit.Assert.*;
import java.util.Arrays;

public class StylistTest {

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

  @Test
  public void Stylist_instantiatesCorrectly_true() {
    Stylist myStylist = new Stylist("Paul Mitchel");
    assertEquals(true, myStylist instanceof Stylist);
  }

  @Test
  public void getName_stylistInstantiatesWithName_String() {
    Stylist myStylist = new Stylist("Paul Mitchel");
    assertEquals("Paul Mitchel", myStylist.getName());
  }

  @Test
  public void all_emptyAtFirst() {
    assertEquals(Stylist.all().size(), 0);
  }
  @Test
  public void equals_returnsTrueIfNamesAretheSame() {
    Client firstClient = new Client("Bob Smith", 1);
    Client secondClient = new Client("Bob Smith", 1);
    assertTrue(firstClient.equals(secondClient));
  }

  @Test
  public void save_assignsIdToObject() {
    Client myClient = new Client("Bob Smith", 1);
    myClient.save();
    Client savedClient = Client.all().get(0);
    assertEquals(myClient.getId(), savedClient.getId());
  }

  @Test
  public void find_findsClientInDatabase_true() {
    Client myClient = new Client("Bob Smith", 1);
    myClient.save();
    Client savedClient = Client.find(myClient.getId());
    assertTrue(myClient.equals(savedClient));
  }

  @Test
  public void save_savesStylistIdIntoDB_true() {
    Stylist myStylist = new Stylist("Paul Mitchel");
    myStylist.save();
    Client myClient = new Client("Bob Smith", myStylist.getId());
    myClient.save();
    Client savedClient = Client.find(myClient.getId());
    assertEquals(savedClient.getStylistId(), myStylist.getId());
  }
  @Test
  public void find_findStylistInDatabase_true() {
    Stylist myStylist = new Stylist("Paul Mitchel");
    myStylist.save();
    Stylist savedStylist = Stylist.find(myStylist.getId());
    assertTrue(myStylist.equals(savedStylist));
  }

  @Test
  public void getClients_retrievesALlClientsFromDatabase_clientsList() {
    Stylist myStylist = new Stylist("Paul Mitchel");
    myStylist.save();
    Client firstClient = new Client("Bob Smith", myStylist.getId());
    firstClient.save();
    Client secondClient = new Client("Bob Smith", myStylist.getId());
    secondClient.save();
    Client[] clients = new Client[] { firstClient, secondClient };
    assertTrue(myStylist.getClients().containsAll(Arrays.asList(clients)));
  }
}
