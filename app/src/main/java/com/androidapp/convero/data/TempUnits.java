package com.androidapp.convero.data;

import java.util.List;
import java.util.Map;

import javax.measure.quantity.Quantity;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;

public class TempUnits {
    // Custom volume units
//    public static final Unit<Volume> TABLESPOON_UK = NonSI.OUNCE_LIQUID_UK.divide(1.6);
//    public static final Unit<Volume> TEASPOON_UK = TABLESPOON_UK.divide(3);
//    public static final Unit<Volume> TABLESPOON_US = NonSI.OUNCE_LIQUID_US.divide(2);
//    public static final Unit<Volume> TEASPOON_US = TABLESPOON_US.divide(3);
//    public static final Unit<Volume> CUP_UK = NonSI.OUNCE_LIQUID_UK.times(10);
//    public static final Unit<Volume> CUP_US = NonSI.OUNCE_LIQUID_US.times(8);

    // Density unit
//    public static final Unit<VolumetricDensity> G_PER_CUP = SI.GRAM.divide(CUP_US).asType(VolumetricDensity.class);

    /**
     * List of all units available for conversion
     */
    private static final List<Unit<? extends Quantity>> UNITS = List.of(
            SI.CELSIUS,
            NonSI.FAHRENHEIT,
            SI.KELVIN
    );

    public static List<Unit<? extends Quantity>> getAll() {
        return UNITS;
    }

    private static final Map<Unit<? extends Quantity>, String> UNIT_TO_STRING_OVERRIDE = Map.ofEntries(
            Map.entry(SI.CELSIUS, "° C"),
            Map.entry(NonSI.FAHRENHEIT, "° F"),
            Map.entry(SI.KELVIN, "K")

    );

    public static String unitToString(Unit<? extends Quantity> unit) {
        String s = UNIT_TO_STRING_OVERRIDE.get(unit);
        return s == null ? unit.toString() : s;
    }
}

