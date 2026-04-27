package aplikacija;

public class MainTest {
	public static void main(String[] args) {
        Database db = new Database();

        Statement s1 = new Statement("CREATE TABLE users (id, name, age)");
        System.out.println(s1.execute(db).getMessage());

        Statement s2 = new Statement("INSERT INTO users VALUES (1, Ana, 25)");
        System.out.println(s2.execute(db).getMessage());

        Statement s3 = new Statement("SELECT * FROM users");
        System.out.println(s3.execute(db).toString());

        Statement s4 = new Statement("DROP TABLE users");
        System.out.println(s4.execute(db).getMessage());

    }
}
