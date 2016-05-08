import org.sql2o.*;
import org.junit.*;
import static org.junit.Assert.*;

public class ClientTest {

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
  public void Client_instantiatesCorrectly_true() {
    Client myClient = new Client("Bob Smith", 1);
    assertEquals(true, myClient instanceof Client);
  }

  @Test
  public void getName_clientInstantiatesWithName_String() {
    Client myClient = new Client("Bob Smith", 1);
    assertEquals("Bob Smith", myClient.getName());
  }

  @Test
  public void all_emptyAtFirst() {
    assertEquals(Client.all().size(), 0);
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


}
