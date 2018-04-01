package ru.drudenko.partnerservices.domain.impl;

import org.dbunit.database.DatabaseDataSourceConnection;
import org.dbunit.dataset.CompositeDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.mssql.InsertIdentityOperation;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.drudenko.partnerservices.domain.AccountIdentity;
import ru.drudenko.partnerservices.domain.ApplicationIdentity;
import ru.drudenko.partnerservices.domain.Customer;
import ru.drudenko.partnerservices.domain.PartnerMapping;
import ru.drudenko.partnerservices.domain.Photo;
import ru.drudenko.partnerservices.domain.Token;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DomainRepositoryImplTest {
    @Autowired
    private DatabaseDataSourceConnection dbUnitConnectionTransport;

    @Autowired
    private DomainRepositoryImpl domainRepository;

    @Before
    public void setUp() throws Exception {

        FlatXmlDataSetBuilder dataSetBuilder = new FlatXmlDataSetBuilder();
        dataSetBuilder.setColumnSensing(true);

        IDataSet testPSDataset = new CompositeDataSet(new IDataSet[]{
                dataSetBuilder.build(getClass().getResourceAsStream("/testdata/test-data.xml"))
        });

        new InsertIdentityOperation(DatabaseOperation.CLEAN_INSERT).execute(dbUnitConnectionTransport, new CompositeDataSet(testPSDataset));
    }

    @Test
    public void findTokenById() throws Exception {
        Token token = domainRepository.findTokenById("666666666");
        assertNotNull(token);

        assertEquals(token.getToken(), "666666666");
        assertEquals(token.getCreated(), Instant.parse("2017-09-09T10:13:13Z"));
        assertEquals(token.getExpired(), Instant.parse("2020-09-09T10:13:13Z"));

        assertNotNull(token.getCustomer());
        assertEquals(token.getCustomer().getId(), Long.valueOf(100));
    }

    @Test
    public void getCustomerById() throws Exception {
        Customer customer = domainRepository.getCustomerById(100L);
        assertNotNull(customer);

        assertEquals(customer.getId(), Long.valueOf(100));
        assertEquals(customer.getFullName(), "Сергей Сергеевич Сергеев");
        assertEquals(customer.getBalance(), BigDecimal.valueOf(123.9));
        assertEquals(customer.isActive(), true);
        assertEquals(customer.getLogin(), "ser123");
        assertEquals(customer.getPassword(), "fhsdifjksdofmdlsfmd,l;fm;lds");

        assertEquals(customer.getPartnerMappings().size(), 2);
    }

    @Test
    public void getPartnerMappingById() {
        PartnerMapping partnerMapping = domainRepository.getPartnerMappingById(102L);
        assertNotNull(partnerMapping);
        assertEquals(partnerMapping.getId(), Long.valueOf(102));
        assertEquals(partnerMapping.getFullName(), "superman");
        assertEquals(partnerMapping.getCustomer().getId(), Long.valueOf(100));
        assertEquals(partnerMapping.getAccountIdentity(), "serser1");
        assertEquals(partnerMapping.getApplicationIdentity(), "vk.com");
        assertNotNull(partnerMapping.getAvatar());
        assertEquals(partnerMapping.getAvatar().getId(), Long.valueOf(104));

    }

    @Test
    public void createPartnerMapping() {
        AccountIdentity accountIdentity = ApplicationIdentity.fromString("testApplicationIdentity").createAccountIdentity("testAccountIdentity");
        PartnerMapping pm = domainRepository.createPartnerMapping(100L, accountIdentity, "fullName");

        PartnerMapping partnerMapping = domainRepository.getPartnerMappingById(pm.getId());
        assertNotNull(partnerMapping);
        assertEquals(partnerMapping.getId(), pm.getId());
        assertEquals(partnerMapping.getFullName(), pm.getFullName());
        assertEquals(partnerMapping.getCustomer().getId(), pm.getCustomer().getId());
        assertEquals(partnerMapping.getAccountIdentity(), pm.getAccountIdentity());
        assertEquals(partnerMapping.getApplicationIdentity(), pm.getApplicationIdentity());
    }

    @Test
    public void deletePartnerMappingById() {
        domainRepository.deletePartnerMappingById(103L);
        PartnerMapping partnerMapping = domainRepository.getPartnerMappingById(103L);
        assertNull(partnerMapping);
    }

    @Test
    public void createPhoto() {
//        assertEquals(partnerMapping.getAvatar().getId(), pm.getAvatar().getId());
        Photo p = domainRepository.createPhoto(103L, "hello world".getBytes(), "test/test");

        Photo photo = domainRepository.getPhoto(p.getId());
        assertNotNull(photo);
        assertEquals(photo.getId(), p.getId());
        assertEquals(photo.getMIMEType(), "test/test");
        assertNotNull(photo.getPhoto());
    }

    @Test
    public void getPhoto() {
        Photo photo = domainRepository.getPhoto(104L);
        assertNotNull(photo);
        assertEquals(photo.getId(), Long.valueOf(104));
        assertEquals(photo.getMIMEType(), "image/jpeg");
        assertNotNull(photo.getPhoto());
    }

    @Test
    public void deletePhoto() {
        domainRepository.deletePhoto(104L);
        Photo photo = domainRepository.getPhoto(104L);
        assertNull(photo);
    }
}