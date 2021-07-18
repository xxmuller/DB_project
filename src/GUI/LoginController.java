package GUI;

import Database.WorkWithDB;
import ORM.RoomORM;
import com.mysql.cj.util.StringUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.persistence.Query;
import java.io.IOException;
import java.sql.*;
import java.util.Calendar;
import java.util.List;

public class LoginController {
    @FXML
    private SwitchScene switchScene = new SwitchScene();
    @FXML
    private TextField textFieldUserName, textFieldPassword; // textove policka na prihlasovacie udaje v LoginScene

    private WorkWithDB workWithDB = new WorkWithDB();

    private boolean checkLoginFromDB(String userName, String password){
        workWithDB.connectToDatabase(); // nadviaze spojenie s db a vytvori tabulky ak nie su vytvorene
            try {
                // tato query z databazy vyberie last_name zamestnanca, ktoreho rola v hoteli je manager alebo receptionist a jeho ide sa rovna premenej userName
                String query = "SELECT last_name FROM "
                        + workWithDB.getDatabase() + ".Employee " +
                        "WHERE (emp_role='manager' OR emp_role='receptionist') AND id=" + Integer.parseInt(userName) + ";";
                workWithDB.setResultSet(workWithDB.getStatement().executeQuery(query)); // pomocou tohto prikazu sa vykona query nad databazou a v resultSet bude ulozeny vysledok
                if (workWithDB.getResultSet().next()) { // resultSet nema funkciu na zistenie, ci je prazdny, tak som to vyriesil takto, pretoze to hadzalo Warning
                    // ak query nasla v databaze zamestnanca podla poziadaviek a jeho last_name sa rovna password, potom metoda vrati true, pretoze su spravne prihlasovacie udaje
                    if (workWithDB.getResultSet().getString("last_name").equals(password))
                        workWithDB.closeConnection();
                        return true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            workWithDB.closeConnection();
            // ak sa prihlasovacie udaje nezhoduju s pozadovanymi, tak nie je mozne sa prihlasit.
            return false;
    }

    private void changeToHomepageScene(ActionEvent actionEvent) throws IOException {
        switchScene.switchToHomepageScene(actionEvent);
    }
    // metoda vrati vcerajsi datum, aby sa aktualizoval atribut availibility v entite room
    public java.sql.Date getYesterdayDate() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new java.sql.Date(Calendar.getInstance().getTimeInMillis()));
        cal.add(Calendar.DATE, -1);

        return new java.sql.Date(cal.getTimeInMillis());
    }
    // ak sa niektora izba uvolnila, tak zmenim jej record v databaze na volnu
    public void checkBookedRooms() {
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory(); // nakonfiguruju sa udaje ohladom ORM z hibernate.cfg.xml
        Session session = sessionFactory.openSession(); // vytvorim session
        session.beginTransaction(); // zacnem transakciu, kde mozem vykonavat pomocou hibernate upravi nad databazou
        // zmeni zaznam vsetkym izbam, ktore maju byt volne
        Query query = session.createNativeQuery("update room R set availibility=?, date_from=?, date_to=? where R.date_to<?");
        query.setParameter(1, 1);
        query.setParameter(2, null);
        query.setParameter(3, null);
        query.setParameter(4, new java.sql.Date(Calendar.getInstance().getTimeInMillis()));
        query.executeUpdate();
        session.getTransaction().commit();
        session.close();
        sessionFactory.close();
    }

    public void loginButtonClicked(ActionEvent actionEvent) throws IOException {
        String userName = textFieldUserName.getText();
        String password = textFieldPassword.getText();

        if (userName.equals("admin") && password.equals("admin")) { // do aplikacie sa prihlasil admin
            changeToHomepageScene(actionEvent);
            Thread thread = new Thread(this::checkBookedRooms); // thread updatne zaznamy izieb, ktore maju byt volne
            thread.start();
        } else { // do aplikacie sa prihlasuje iny pouzivatel a z tabulky employee sa zisti, ci ma na to opravnenie
            if (!(userName.equals("")) && !(password.equals(""))) { // ak boli zadane udaje, tak skusi prihlasit
                try { // tento try-catch zabezpeci, ze userName bude int, pretoze potom podla neho vyhladavame
                    Integer.parseInt(userName);
                    if (checkLoginFromDB(userName, password)) { // metoda zisti, ci su platne prihlasovacie udaje, ak ano zmeni sa scena, kde je prihlaseny uzivatel
                        changeToHomepageScene(actionEvent);
                        Thread thread = new Thread(this::checkBookedRooms); // thread updatne zaznamy izieb, ktore maju byt volne
                        thread.start();
                    } else // boli neplatne prihlasovacie udaje, zatedy vypise iba do konzoly
                        System.out.println("Access Denied.");
                } catch (NumberFormatException e) {
                    System.out.println("Nespravne udaje.");
                }
            } else
                System.out.println("Zadajte udaje.");
        }
    }
}
