package de.janthomae.leiningenplugin.leiningen;

import junit.framework.Assert;
import org.junit.Test;

/**
 * @author <a href="janthomae@janthomae.de">Jan Thom&auml;</a>
 * @version $Id:$
 */
public class LeiningenProjectFileTest {
    @Test
    public void testLoadFile() {
        LeiningenProjectFile lpf = new LeiningenProjectFile("project.clj");
        String name = lpf.getName();
        Assert.assertEquals("Name was wrong", "de.janthomae/leiningenplugin", name);
    }
}
