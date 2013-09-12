import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


@RunWith(Suite.class)
@SuiteClasses({ ComponentHRTest.class, CoverPageTest.class, EndItemTest.class,
        TemplateLoaderTest.class, UnitHRTest.class, PropertyBookTest.class })
public class AllTests {

}
