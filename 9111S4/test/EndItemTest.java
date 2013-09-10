import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.hotan.ninetripleone.supply.model.EndItem;


public class EndItemTest {

    private EndItem mItem, mItem2;
    
    @Before
    public void setUp() throws Exception {
        mItem = new EndItem("name", "lin", "nsn");
        mItem2 = new EndItem("name", "lin", "nsn");
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testEqualsReq() {
        assertEquals(mItem, mItem2);
    }

    @Test
    public void testNotEqualAfterSerial() {
        mItem2.setSn("Serialnumber");
        assertNotSame(mItem, mItem2);
    }
}
