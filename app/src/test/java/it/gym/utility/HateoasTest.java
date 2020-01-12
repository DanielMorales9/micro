package it.gym.utility;

import it.gym.model.*;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class HateoasTest {

    public static void expectAdmin(ResultActions result, Admin admin, String prefix) throws Exception {
        prefix = handlePrefix(prefix);
        expectUser(result, admin, prefix);
    }

    public static void expectAdmin(ResultActions result, Admin admin) throws Exception {
        expectAdmin(result, admin, null);
    }

    public static void expectAdminRoles(ResultActions result, List<Role> roles) throws Exception {
        expectAdminRoles(result, roles, null);
    }

    public static void expectAdminRoles(ResultActions result, List<Role> roles, String prefix) throws Exception {
        prefix = handlePrefixForArray(prefix);

        for (int i = 0; i < 3; i++) {
            result = result
                    .andExpect(jsonPath("$"+prefix+"["+i+"].id").value(roles.get(i).getId()))
                    .andExpect(jsonPath("$"+prefix+"["+i+"].name").value(roles.get(i).getName()));
        }

    }

    public static void expectGym(ResultActions result, Gym gym, String prefix) throws Exception {
        prefix = handlePrefix(prefix);
        result.andExpect(jsonPath("$"+prefix+"id").value(gym.getId()))
                .andExpect(jsonPath("$"+prefix+"name").value(gym.getName()))
//                .andExpect(jsonPath("$"+prefix+"weekStartsOn").value(gym.getWeekStartsOn()))
                .andExpect(jsonPath("$"+prefix+"mondayStartHour").value(gym.getMondayStartHour()))
                .andExpect(jsonPath("$"+prefix+"tuesdayStartHour").value(gym.getTuesdayStartHour()))
                .andExpect(jsonPath("$"+prefix+"wednesdayStartHour").value(gym.getWednesdayStartHour()))
                .andExpect(jsonPath("$"+prefix+"thursdayStartHour").value(gym.getThursdayStartHour()))
                .andExpect(jsonPath("$"+prefix+"fridayStartHour").value(gym.getFridayStartHour()))
                .andExpect(jsonPath("$"+prefix+"saturdayStartHour").value(gym.getSaturdayStartHour()))
                .andExpect(jsonPath("$"+prefix+"sundayStartHour").value(gym.getSundayStartHour()))
                .andExpect(jsonPath("$"+prefix+"mondayEndHour").value(gym.getMondayEndHour()))
                .andExpect(jsonPath("$"+prefix+"tuesdayEndHour").value(gym.getTuesdayEndHour()))
                .andExpect(jsonPath("$"+prefix+"wednesdayEndHour").value(gym.getWednesdayEndHour()))
                .andExpect(jsonPath("$"+prefix+"thursdayEndHour").value(gym.getThursdayEndHour()))
                .andExpect(jsonPath("$"+prefix+"fridayEndHour").value(gym.getFridayEndHour()))
                .andExpect(jsonPath("$"+prefix+"saturdayEndHour").value(gym.getSaturdayEndHour()))
                .andExpect(jsonPath("$"+prefix+"sundayEndHour").value(gym.getSundayEndHour()))
                .andExpect(jsonPath("$"+prefix+"mondayOpen").value(gym.isMondayOpen()))
                .andExpect(jsonPath("$"+prefix+"tuesdayOpen").value(gym.isTuesdayOpen()))
                .andExpect(jsonPath("$"+prefix+"wednesdayOpen").value(gym.isWednesdayOpen()))
                .andExpect(jsonPath("$"+prefix+"thursdayOpen").value(gym.isThursdayOpen()))
                .andExpect(jsonPath("$"+prefix+"fridayOpen").value(gym.isFridayOpen()))
                .andExpect(jsonPath("$"+prefix+"saturdayOpen").value(gym.isSaturdayOpen()))
                .andExpect(jsonPath("$"+prefix+"sundayOpen").value(gym.isSundayOpen()));
    }

    public static void expectGym(ResultActions result, Gym gym) throws Exception {
        expectGym(result, gym, null);
    }

    private static String handlePrefixForArray(String prefix) {
        return handlePrefix(prefix, true);
    }

    private static String handlePrefix(String prefix) {
        return handlePrefix(prefix, false);
    }

    private static String handlePrefix(String prefix, boolean isArray) {
        prefix = handleNullPrefix(prefix);
        if (!prefix.equals("")) {
            if (!isArray) {
                prefix = prefix.endsWith(".") ? prefix : prefix + ".";
            }
            prefix = prefix.startsWith(".") ? prefix : "." + prefix;
        }
        else if (!isArray) {
            prefix = ".";
        }
        return prefix;
    }

    private static String handleNullPrefix(String prefix) {
        prefix = prefix == null ? "" : prefix;
        return prefix;
    }

    public static void expectTrainingBundleSpec(ResultActions result,
                                                PersonalTrainingBundleSpecification bundle) throws Exception {
        expectTrainingBundleSpec(result, bundle, null);
    }

    private static void expectATrainingBundleSpec(ResultActions result,
                                                          ATrainingBundleSpecification bundle,
                                                          String prefix) throws Exception {
        result
                .andExpect(jsonPath("$"+prefix+"id").value(bundle.getId()))
                .andExpect(jsonPath("$"+prefix+"name").value(bundle.getName()))
                .andExpect(jsonPath("$"+prefix+"price").value(bundle.getPrice()))
                .andExpect(jsonPath("$"+prefix+"disabled").value(bundle.getDisabled()))
                .andExpect(jsonPath("$"+prefix+"description").value(bundle.getDescription()));
    }

    public static void expectTrainingBundleSpec(ResultActions result,
                                                PersonalTrainingBundleSpecification bundle,
                                                String prefix) throws Exception {
        prefix = handlePrefix(prefix, false);
        expectATrainingBundleSpec(result, bundle, prefix);
        result.andExpect(jsonPath("$"+prefix+"numSessions").value(bundle.getNumSessions()));
    }

    public static void expectTrainingBundleSpec(ResultActions result,
                                                CourseTrainingBundleSpecification bundle) throws Exception {
        expectTrainingBundleSpec(result, bundle, null);
    }

    public static void expectTrainingBundleSpec(ResultActions result,
                                                CourseTrainingBundleSpecification bundle,
                                                String prefix) throws Exception {
        prefix = handlePrefix(prefix, false);
        expectATrainingBundleSpec(result, bundle, prefix);
        result
            .andExpect(jsonPath("$"+prefix+"number").value(bundle.getNumber()))
            .andExpect(jsonPath("$"+prefix+"maxCustomers").value(bundle.getMaxCustomers()));
    }

    public static void expectSale(ResultActions result, Sale sale) throws Exception {
        expectSale(result, sale, null);
    }

    public static void expectSale(ResultActions result,
                                  Sale sale,
                                  String prefix) throws Exception {
        prefix = handlePrefix(prefix, false);
        result
                .andExpect(jsonPath("$"+prefix+"id").value(sale.getId()))
                .andExpect(jsonPath("$"+prefix+"amountPayed").value(sale.getAmountPayed()))
                .andExpect(jsonPath("$"+prefix+"totalPrice").value(sale.getTotalPrice()));

    }

    public static void expectCustomer(ResultActions result, Customer customer, String prefix) throws Exception {
        prefix = handlePrefix(prefix, false);
        expectUser(result, customer, prefix);
        result
                .andExpect(jsonPath("$"+prefix+"height").value(customer.getHeight()))
                .andExpect(jsonPath("$"+prefix+"weight").value(customer.getWeight()));
    }

    public static void expectCustomer(ResultActions result, Customer customer) throws Exception {
        expectCustomer(result, customer, null);
    }

    public static void expectUser(ResultActions result, AUser user, String prefix) throws Exception {
        prefix = handlePrefix(prefix);
        result
                .andExpect(jsonPath("$"+prefix+"id").value(user.getId()))
                .andExpect(jsonPath("$"+prefix+"email").value(user.getEmail()))
                .andExpect(jsonPath("$"+prefix+"firstName").value(user.getFirstName()))
                .andExpect(jsonPath("$"+prefix+"lastName").value(user.getLastName()))
                .andExpect(jsonPath("$"+prefix+"verified")  .value(user.isVerified()));
    }

    public static void expectedSalesLineItem(ResultActions result, SalesLineItem expected) throws Exception {
        expectedSalesLineItem(result, expected, null);
    }

    public static void expectedSalesLineItem(ResultActions result,
                                             SalesLineItem expected,
                                             String prefix) throws Exception {
        prefix = handlePrefix(prefix);
        result.andExpect(jsonPath("$"+prefix+"id").value(expected.getId()));
    }

    private static void expectATrainingBundle(ResultActions result,
                                            ATrainingBundle trainingBundle,
                                            String prefix) throws Exception {
        result.andExpect(jsonPath("$"+prefix+"id").value(trainingBundle.getId()))
                .andExpect(jsonPath("$"+prefix+"name").value(trainingBundle.getName()));
    }

    public static void expectTrainingBundle(ResultActions result,
                                            CourseTrainingBundle trainingBundle) throws Exception {
        expectTrainingBundle(result, trainingBundle, null);
    }

    public static void expectTrainingBundle(ResultActions result,
                                            PersonalTrainingBundle trainingBundle,
                                            String prefix) throws Exception {
        prefix = handlePrefix(prefix);
        expectATrainingBundle(result, trainingBundle, prefix);
    }

    public static void expectTrainingBundle(ResultActions result,
                                            CourseTrainingBundle trainingBundle,
                                            String prefix) throws Exception {
        prefix = handlePrefix(prefix);
        // TODO expect startTime and endTime
        expectATrainingBundle(result, trainingBundle, prefix);
    }

    public static void expectCustomerRoles(ResultActions result,
                                           List<Role> roles,
                                           String prefix) throws Exception {
        prefix = handlePrefixForArray(prefix);

        for (int i = 0; i < 1; i++) {
            result = result
                    .andExpect(jsonPath("$"+prefix+"["+i+"].id").value(roles.get(i).getId()))
                    .andExpect(jsonPath("$"+prefix+"["+i+"].name").value(roles.get(i).getName()));
        }

    }

    public static void expectEvent(ResultActions result, AEvent event) throws Exception {
        expectEvent(result, event, null);
    }

    private static void expectEvent(ResultActions result, AEvent event, String prefix) throws Exception {
        prefix = handlePrefix(prefix);
        result.andExpect(jsonPath("$"+prefix+"id").value(event.getId()))
                .andExpect(jsonPath("$"+prefix+"name").value(event.getName()));
    }

    public static void expectReservation(ResultActions result, Reservation reservation) throws Exception {
        expectReservation(result, reservation, null);
    }

    public static void expectReservation(ResultActions result, Reservation reservation, String prefix) throws Exception {
        prefix = handlePrefix(prefix);
        result.andExpect(jsonPath("$"+prefix+"id").value(reservation.getId()))
                .andExpect(jsonPath("$"+prefix+"confirmed").value(reservation.getConfirmed()));
    }

}
