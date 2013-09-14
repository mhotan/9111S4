package com.hotan.ninetripleone.supply.forms;

import java.text.SimpleDateFormat;
import java.util.Date;

public class NoDeficiencyEntry extends DeficiencyEntryBase {

    public NoDeficiencyEntry() {
        super("", new SimpleDateFormat("yyyy-mm-dd").format(new Date()), "", STATUS.OK);
    }

}
