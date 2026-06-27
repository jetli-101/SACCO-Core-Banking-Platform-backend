package com.example.sacco_core_banking.classes;

import java.util.Set;

/**
 * The 47 counties of Kenya, as established under Article 6 and the First Schedule of the
 * Constitution. Used to validate the `county` field on member registration so SACCOs
 * can't end up with free-text typos in a field reports get grouped by.
 */
public final class KenyaCounties {

    private KenyaCounties() {
    }

    public static final Set<String> NAMES = Set.of(
            "Mombasa", "Kwale", "Kilifi", "Tana River", "Lamu", "Taita Taveta",
            "Garissa", "Wajir", "Mandera", "Marsabit", "Isiolo", "Meru",
            "Tharaka-Nithi", "Embu", "Kitui", "Machakos", "Makueni", "Nyandarua",
            "Nyeri", "Kirinyaga", "Murang'a", "Kiambu", "Turkana", "West Pokot",
            "Samburu", "Trans-Nzoia", "Uasin Gishu", "Elgeyo-Marakwet", "Nandi",
            "Baringo", "Laikipia", "Nakuru", "Narok", "Kajiado", "Kericho",
            "Bomet", "Kakamega", "Vihiga", "Bungoma", "Busia", "Siaya",
            "Kisumu", "Homa Bay", "Migori", "Kisii", "Nyamira", "Nairobi"
    );

    public static boolean isValid(String county) {
        if (county == null) {
            return false;
        }
        return NAMES.stream().anyMatch(name -> name.equalsIgnoreCase(county.trim()));
    }
}
