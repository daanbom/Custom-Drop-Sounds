package org.CustomSounds;

import net.runelite.client.plugins.grounditems.config.ValueCalculationMode;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CustomSoundsPluginOverflowTest
{
    @Test
    public void getValueByMode_handlesLargeStackValuesWithoutOverflow()
    {
        long gePrice = 2_147_483_648L;
        long haPrice = 1_000_000_000L;

        assertEquals(2_147_483_648L, CustomSoundsPlugin.getValueByMode(gePrice, haPrice, ValueCalculationMode.GE));
        assertEquals(2_147_483_648L, CustomSoundsPlugin.getValueByMode(gePrice, haPrice, ValueCalculationMode.HIGHEST));
        assertEquals(1_000_000_000L, CustomSoundsPlugin.getValueByMode(gePrice, haPrice, ValueCalculationMode.HA));
    }
}
