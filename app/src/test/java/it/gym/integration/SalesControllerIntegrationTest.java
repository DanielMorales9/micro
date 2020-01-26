package it.gym.integration;


import it.gym.model.*;
import it.gym.repository.*;
import it.gym.utility.Calendar;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static it.gym.utility.Calendar.getNextMonday;
import static it.gym.utility.Fixture.*;
import static it.gym.utility.HateoasTest.*;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SalesControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired private RoleRepository roleRepository;
    @Autowired private SaleRepository repository;
    @Autowired private SalesLineItemRepository sliRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private GymRepository gymRepository;
    @Autowired private TrainingBundleSpecificationRepository bundleSpecRepository;
    @Autowired private TrainingBundleRepository bundleRepository;
    @Autowired private PaymentRepository payRepository;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private Customer customer;
    private Sale sale;
    private PersonalTrainingBundleSpecification personalSpec;
    private CourseTrainingBundleSpecification courseSpec;

    private SalesLineItem sli1;

    @Before
    public void before() {
        List<Role> roles = createCustomerRoles();
        roles = roleRepository.saveAll(roles);
        customer = createCustomer(1L,
                "customer@customer.com",
                "password",
                "customer",
                "customer",
                true,
                roles
        );
        customer = userRepository.save(customer);
        sale = createSale(1L, customer);
        personalSpec = createPersonalBundleSpec(1L, "personal", 11);
        courseSpec = createCourseBundleSpec(1L, "course", 11, 1, 111.);
        personalSpec = bundleSpecRepository.save(personalSpec);
        courseSpec = bundleSpecRepository.save(courseSpec);
        TimeOption option = courseSpec.getOptions().toArray(new TimeOption[]{})[0];
        CourseTrainingBundle course = createCourseBundle(1L, getNextMonday(), courseSpec, option);
        course = bundleRepository.save(course);

        sli1 = sale.addSalesLineItem(course);
        sale = repository.save(sale);
        sli1 = sale.getSalesLineItems().get(0);

    }

    @After
    public void after() {
        sliRepository.deleteAll();
        payRepository.deleteAll();
        repository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
        gymRepository.deleteAll();
        bundleRepository.deleteAll();
        bundleSpecRepository.deleteAll();
    }

    @Test
    public void findSaleByIdOK() throws Exception {
        ResultActions result = mockMvc.perform(get("/sales/" + sale.getId()))
                .andExpect(status().isOk());

        expectSale(result, sale);
        expectUser(result, customer, "customer");
    }

    @Test
    public void findSaleByIdGotNotFound() throws Exception {
        mockMvc.perform(get("/sales/" + 1000))
                .andExpect(status().isNotFound());
    }

    @Test
    public void findSaleUserByIdOK() throws Exception {
        ResultActions result = mockMvc.perform(get("/sales/" + sale.getId() + "/customer"))
                .andExpect(status().isOk());
        expectUser(result, customer);
    }

    @Test
    public void whenCreateSaleOK() throws Exception {
        String path = "/sales/createSale/" + customer.getId();

        ResultActions result = mockMvc.perform(get(path))
                .andExpect(status().isOk());

        Sale s = repository.findAll()
                .stream()
                .filter(sale1 -> !sale.getId().equals(sale1.getId()))
                .limit(1)
                .collect(Collectors.toList()).get(0);

        Sale expected = new Sale();
        expected.setId(s.getId());
        expected.setAmountPayed(0.);
        expected.setCompleted(false);
        expected.setCustomer(customer);

        expectSale(result, expected);
        expectUser(result, customer, "customer");
    }


    @Test
    public void whenAddSliOK() throws Exception {
        String path = "/sales/addSalesLineItem";

        ResultActions result = mockMvc.perform(get(path)
                .param("bundleSpecId", personalSpec.getId().toString())
                .param("saleId", sale.getId().toString()))
                .andExpect(status().isOk());

        List<SalesLineItem> sli = sliRepository.findAll();

        Sale expected = new Sale();
        expected.setId(sale.getId());
        expected.setAmountPayed(0.);
        expected.setCompleted(false);
        expected.setCustomer(customer);
        expected.setSalesLineItems(sli);

        expectSale(result, expected);
        expectSalesLineItems(result, sli, "salesLineItems");

        expectUser(result, customer, "customer");

    }

    @Test
    public void whenAddSliByOptionOK() throws Exception {
        TimeOption option = courseSpec.getOptions().toArray(new TimeOption[]{})[0];
        String path = "/sales/addSalesLineItem";

        ResultActions result = mockMvc.perform(get(path)
                .param("bundleSpecId", courseSpec.getId().toString())
                .param("saleId", sale.getId().toString())
                .param("optionId", option.getId().toString()))
                .andExpect(status().isOk());

        List<SalesLineItem> sli = sliRepository.findAll();

        Sale expected = new Sale();
        expected.setId(sale.getId());
        expected.setAmountPayed(0.);
        expected.setCompleted(false);
        expected.setCustomer(customer);
        expected.setSalesLineItems(sli);

        expectSale(result, expected);
        expectSalesLineItems(result, sli, "salesLineItems");

        expectUser(result, customer, "customer");

    }

    @Test
    public void whenAddSliThenItFails() throws Exception {
        String path = "/sales/addSalesLineItem";

        mockMvc.perform(get(path)
                        .param("bundleSpecId", courseSpec.getId().toString())
                        .param("saleId", sale.getId().toString()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenDeleteSliOK() throws Exception {
        String path = "/sales/deleteSalesLineItem/" + sale.getId() + "/" + sli1.getId();

        ResultActions result = mockMvc.perform(delete(path))
                .andExpect(status().isOk());

        ArrayList<SalesLineItem> sli = new ArrayList<>();

        Sale expected = new Sale();
        expected.setId(sale.getId());
        expected.setAmountPayed(0.);
        expected.setCompleted(false);
        expected.setCustomer(customer);

        expectSale(result, expected);
        result = result.andExpect(jsonPath("$.salesLineItems").value(sli));
        expectUser(result, customer, "customer");
        result.andExpect(jsonPath("$.customer.currentTrainingBundles").isEmpty());
        assertThat(sliRepository.findAll()).isEmpty();
        logger.info(bundleRepository.findAll().toString());
        assertThat(bundleRepository.findAll().size()).isEqualTo(0);
        bundleSpecRepository.findById(courseSpec.getId()).get();
    }

    @Test
    public void whenConfirmSaleOK() throws Exception {
        String path = "/sales/confirmSale/" + sale.getId();

        ResultActions result = mockMvc.perform(get(path))
                .andExpect(status().isOk());

        List<SalesLineItem> sli = sliRepository.findAll();

        Sale expected = new Sale();
        expected.setId(sale.getId());
        expected.setAmountPayed(0.);
        expected.setCompleted(true);
        expected.setCustomer(customer);
        expected.setSalesLineItems(sli);

        expectSale(result, expected);
        expectSalesLineItems(result, sli, "salesLineItems");

        Customer c = customer;
        expectUser(result, c, "customer");
        c.setCurrentTrainingBundles(null);
        userRepository.save(c);

    }

    @Test
    public void whenFindSalesLineItemsOK() throws Exception {
        String path = "/sales/" + sale.getId() + "/salesLineItems";

        ResultActions result = mockMvc.perform(get(path))
                .andExpect(status().isOk());

        List<SalesLineItem> sli = sliRepository.findAll();

        expectSalesLineItems(result, sli, "");
    }

    @Test
    public void whenDeleteSaleOK() throws Exception {
        String path = "/sales/" + sale.getId();

        List<SalesLineItem> sli = sliRepository.findAll();
        ResultActions result = mockMvc.perform(delete(path))
                .andExpect(status().isOk());


        Sale expected = new Sale();
        expected.setId(sale.getId());
        expected.setAmountPayed(0.);
        expected.setCompleted(true);
        expected.setCustomer(customer);
        expected.setSalesLineItems(sli);

        expectSale(result, expected);
        expectSalesLineItems(result, sli, "salesLineItems");

        Customer c = customer;
        expectUser(result, c, "customer");
        result.andExpect(jsonPath("$.customer.currentTrainingBundles").isEmpty());
        assertThat(repository.findAll()).isEmpty();
        assertThat(sliRepository.findAll()).isEmpty();
        assertThat(bundleRepository.findAll().size()).isEqualTo(0);
        bundleSpecRepository.findById(courseSpec.getId()).get();
    }

    @Test
    public void whenGetTotalPriceOK() throws Exception {
        String path = "/sales/getTotalPrice/"+sale.getId();

        ResultActions result = mockMvc.perform(get(path))
                .andExpect(status().isOk());

        List<SalesLineItem> sli = sliRepository.findAll();

        Sale expected = new Sale();
        expected.setId(sale.getId());
        expected.setAmountPayed(0.);
        expected.setCompleted(true);
        expected.setCustomer(customer);
        expected.setSalesLineItems(sli);

        expectSale(result, expected);
        expectSalesLineItems(result, sli, "salesLineItems");
        expectUser(result, customer, "customer");

    }

    @Test
    public void whenSearchByDateAndIdOK() throws Exception {
        String path = "/sales/searchByDateAndId?id="+sale.getCustomer().getId()+"&date="+Calendar.yesterday("dd-MM-yyyy");

        ResultActions result = mockMvc.perform(get(path))
                .andExpect(status().isOk());

        expectSales(result);
    }

    @Test
    public void whenSearchByDateAndIdAndPayedOK() throws Exception {
        String path = "/sales/searchByDateAndId?payed=false&id="+sale.getCustomer().getId()+"&date="+Calendar.yesterday("dd-MM-yyyy");

        ResultActions result = mockMvc.perform(get(path))
                .andExpect(status().isOk());

        expectSales(result);
    }

    private void expectSales(ResultActions result) throws Exception {
        List<SalesLineItem> sli = sliRepository.findAll();
        for (int i = 0; i < 1; i++) {

            Sale expected = new Sale();
            expected.setId(sale.getId());
            expected.setAmountPayed(0.);
            expected.setCompleted(true);
            expected.setCustomer(customer);
            expected.setSalesLineItems(sli);

            expectSale(result, expected, "content[" + i + "]");
            expectSalesLineItems(result, sli, "content[" + i + "].salesLineItems");
            expectUser(result, customer, "content[" + i + "].customer");
        }
    }

    @Test
    public void whenPayOK() throws Exception {
        String path = "/sales/pay/"+sale.getId()+"?amount="+11.0;

        sale.setCompleted(true);
        sale = repository.save(sale);

        ResultActions result = mockMvc.perform(get(path))
                .andExpect(status().isOk());

        List<SalesLineItem> sli = sliRepository.findAll();
        List<Payment> payments = payRepository.findAll();

        logger.info(payments.toString());

        Payment payment = payments.get(0);

        Sale expected = new Sale();
        expected.setId(sale.getId());
        expected.setAmountPayed(111.);
        expected.setCompleted(true);
        expected.setPayed(true);
        expected.setCustomer(customer);
        expected.setSalesLineItems(sli);

        expectSalesLineItems(result, sli, "salesLineItems");
        expectUser(result, customer, "customer");
        expectPayment(result, payment, "payments[0]");

    }

    @Test
    public void whenFindCustomerBySaleOK() throws Exception {
        String path = "/sales/" + sale.getId() + "/customer";

        ResultActions result = mockMvc.perform(get(path))
                .andExpect(status().isOk());

        expectUser(result, customer);
    }

    @Test
    public void whenFindAllOK() throws Exception {
        String path = "/sales/";

        ResultActions result = mockMvc.perform(get(path))
                .andExpect(status().isOk());

        expectSales(result);
    }

    @Test
    public void whenFindAllPayedOK() throws Exception {
        String path = "/sales?payed=false";

        ResultActions result = mockMvc.perform(get(path))
                .andExpect(status().isOk());

        expectSales(result);
    }

    @Test
    public void whenFindUserSalesOK() throws Exception {
        String path = "/sales/findUserSales?id="+customer.getId();

        ResultActions result = mockMvc.perform(get(path))
                .andExpect(status().isOk());

        expectSales(result);
    }

    @Test
    public void whenFindUserSalesAndPayedOK() throws Exception {
        String path = "/sales/findUserSales?payed=false&id="+customer.getId();

        ResultActions result = mockMvc.perform(get(path))
                .andExpect(status().isOk());

        expectSales(result);
    }

    @Test
    public void whenSearchByDateOK() throws Exception {
        String path = "/sales/searchByDate?date="+ Calendar.today("dd-MM-yyyy");

        ResultActions result = mockMvc.perform(get(path))
                .andExpect(status().isOk());

        expectSales(result);
    }

    @Test
    public void whenSearchByLastNameAndDateOK() throws Exception {
        String path = "/sales/searchByLastNameAndDate?lastName="+customer.getLastName()+"&date="
                +Calendar.yesterday("dd-MM-yyyy");

        ResultActions result = mockMvc.perform(get(path))
                .andExpect(status().isOk());

        expectSales(result);
    }

    @Test
    public void whenSearchByLastNameAndDateAndPayedOK() throws Exception {
        String path = "/sales/searchByLastNameAndDate?payed=false&lastName="+customer.getLastName()+"&date="
                +Calendar.yesterday("dd-MM-yyyy");

        ResultActions result = mockMvc.perform(get(path))
                .andExpect(status().isOk());

        expectSales(result);
    }

    @Test
    public void whenSearchByLastNameOK() throws Exception {
        String path = "/sales/searchByLastName?lastName="+customer.getLastName();

        ResultActions result = mockMvc.perform(get(path))
                .andExpect(status().isOk());

        expectSales(result);
    }

    @Test
    public void whenSearchByLastNameAndPayedOK() throws Exception {
        String path = "/sales/searchByLastName?payed=false&lastName="+customer.getLastName();

        ResultActions result = mockMvc.perform(get(path))
                .andExpect(status().isOk());

        expectSales(result);
    }


    private void expectSalesLineItems(ResultActions result, List<SalesLineItem> sli, String prefix) throws Exception {
        for (int i = 0; i < sli.size(); i++) {
            SalesLineItem sl = sli.get(i);
            expectedSalesLineItem(result, sl, prefix+"[" + i + "]");
        }
    }

}
