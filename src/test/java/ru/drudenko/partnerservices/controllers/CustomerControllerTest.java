package ru.drudenko.partnerservices.controllers;

import io.restassured.RestAssured;
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
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CustomerControllerTest {
    @LocalServerPort
    int port;

    @Autowired
    private DatabaseDataSourceConnection dbUnitConnectionTransport;

    @Before
    public void setUp() throws Exception {

        FlatXmlDataSetBuilder dataSetBuilder = new FlatXmlDataSetBuilder();
        dataSetBuilder.setColumnSensing(true);

        IDataSet testPSDataset = new CompositeDataSet(new IDataSet[]{
                dataSetBuilder.build(getClass().getResourceAsStream("/testdata/test-data.xml"))
        });

        new InsertIdentityOperation(DatabaseOperation.CLEAN_INSERT).execute(dbUnitConnectionTransport, new CompositeDataSet(testPSDataset));

        RestAssured.port = port;
        RestAssured.basePath = "/";
    }

    @Test
    public void getCustomer() throws Exception {
        given().header("authorization", "Bearer 666666666")
                .get("/customer/100")
                .then().assertThat()
                .statusCode(200)
                .body("fullName", equalTo("Сергей Сергеевич Сергеев"))
                .body("active", equalTo(true))
                .body("balance", equalTo(123.9F));
    }

    @Test
    public void getCurrentCustomer() throws Exception {
        given().header("authorization", "Bearer 666666666")
                .get("/customer/@me")
                .then().assertThat()
                .statusCode(200)
                .body("fullName", equalTo("Сергей Сергеевич Сергеев"))
                .body("active", equalTo(true))
                .body("balance", equalTo(123.9F));
    }

    @Test
    public void deletePartnerMapping() throws Exception {
        given().header("authorization", "Bearer 666666666")
                .delete("/customer/100/partnermapping/102")
                .then().assertThat()
                .statusCode(200);


        given().header("authorization", "Bearer 666666666")
                .delete("/customer/100/partnermapping/110")
                .then().assertThat()
                .statusCode(404);
    }

    @Test
    public void deleteCurrentCustomerPartnerMapping() throws Exception {
        given().header("authorization", "Bearer 666666666")
                .delete("/customer/@me/partnermapping/102")
                .then().assertThat()
                .statusCode(200);


        given().header("authorization", "Bearer 666666666")
                .delete("/customer/@me/partnermapping/110")
                .then().assertThat()
                .statusCode(404);
    }

    @Test
    public void getPartnerMapping() throws Exception {
        given().header("authorization", "Bearer 666666666")
                .get("/customer/100/partnermapping/102")
                .then().assertThat()
                .statusCode(200)
                .body("fullName", equalTo("superman"))
                .body("id", equalTo(102))
                .body("customerId", equalTo(100))
                .body("applicationIdentity", equalTo("vk.com"))
                .body("accountIdentity", equalTo("serser1"))
                .body("avatar.id", equalTo(104))
                .body("avatar.mimetype", equalTo("image/jpeg"))
                .body("avatar.photo", notNullValue());

        given().header("authorization", "Bearer 666666666")
                .get("/customer/100/partnermapping/111")
                .then().assertThat()
                .statusCode(404);

    }

    @Test
    public void getCurrentCustomerPartnerMapping() throws Exception {
        given().header("authorization", "Bearer 666666666")
                .get("/customer/@me/partnermapping/102")
                .then().assertThat()
                .statusCode(200)
                .body("fullName", equalTo("superman"))
                .body("id", equalTo(102))
                .body("customerId", equalTo(100))
                .body("applicationIdentity", equalTo("vk.com"))
                .body("accountIdentity", equalTo("serser1"))
                .body("avatar.id", equalTo(104))
                .body("avatar.mimetype", equalTo("image/jpeg"))
                .body("avatar.photo", notNullValue());

        given().header("authorization", "Bearer 666666666")
                .get("/customer/@me/partnermapping/111")
                .then().assertThat()
                .statusCode(404);
    }

    @Test
    public void createPartnerMapping() throws Exception {
        String json = "{\n" +
                "    \"fullName\": \"fullName\",\n" +
                "    \"accountIdentity\": \"accountIdentity\",\n" +
                "    \"applicationIdentity\": \"applicationIdentity\"\n" +
                "}";
        given().header("authorization", "Bearer 666666666")
                .body(json)
                .contentType("application/json")
                .post("/customer/100/partnermapping")
                .then().assertThat()
                .statusCode(201)
                .body("fullName", equalTo("fullName"))
                .body("customerId", equalTo(100))
                .body("applicationIdentity", equalTo("applicationIdentity"))
                .body("accountIdentity", equalTo("accountIdentity"));
    }

    @Test
    public void createCurrentCustomerPartnerMapping() throws Exception {
        String json = "{\n" +
                "    \"fullName\": \"fullName\",\n" +
                "    \"accountIdentity\": \"accountIdentity\",\n" +
                "    \"applicationIdentity\": \"applicationIdentity\"\n" +
                "}";
        given().header("authorization", "Bearer 666666666")
                .body(json)
                .contentType("application/json")
                .post("/customer/@me/partnermapping")
                .then().assertThat()
                .statusCode(201)
                .body("fullName", equalTo("fullName"))
                .body("customerId", equalTo(100))
                .body("applicationIdentity", equalTo("applicationIdentity"))
                .body("accountIdentity", equalTo("accountIdentity"));
    }

    @Test
    public void updatePartnerMapping() throws Exception {
        String json = "{\n" +
                "    \"fullName\": \"fullName\",\n" +
                "    \"accountIdentity\": \"accountIdentity\",\n" +
                "    \"applicationIdentity\": \"applicationIdentity\"\n" +
                "}";
        given().header("authorization", "Bearer 666666666")
                .body(json)
                .contentType("application/json")
                .put("/customer/100/partnermapping/102")
                .then().assertThat()
                .statusCode(200);
    }

    @Test
    public void updateCurrentCustomerPartnerMapping() throws Exception {
        String json = "{\n" +
                "    \"fullName\": \"fullName\",\n" +
                "    \"accountIdentity\": \"accountIdentity\",\n" +
                "    \"applicationIdentity\": \"applicationIdentity\"\n" +
                "}";
        given().header("authorization", "Bearer 666666666")
                .body(json)
                .contentType("application/json")
                .put("/customer/@me/partnermapping/102")
                .then().assertThat()
                .statusCode(200);
    }

    @Test
    public void addAvatar() throws Exception {

        given().header("authorization", "Bearer 666666666")
                .multiPart("avatar", "temp.jpg", "hello world".getBytes(), "image/jpeg")
                .post("/customer/100/partnermapping/103/avatar")
                .then().assertThat()
                .statusCode(201)
                .body("id", notNullValue());
    }

    @Test
    public void addCurrentCustomerAvatar() throws Exception {
        given().header("authorization", "Bearer 666666666")
                .multiPart("avatar", "temp.jpg", "hello world".getBytes(), "image/jpeg")
                .post("/customer/@me/partnermapping/103/avatar")
                .then().assertThat()
                .statusCode(201)
                .body("id", notNullValue());
    }

    @Test
    public void updateAvatar() throws Exception {
        given().header("authorization", "Bearer 666666666")
                .multiPart("avatar", "temp.jpg", "hello world2".getBytes(), "image/jpeg")
                .put("/customer/100/partnermapping/102/avatar")
                .then().assertThat()
                .statusCode(200);
    }

    @Test
    public void updateCurrentCustomerAvatar() throws Exception {
        given().header("authorization", "Bearer 666666666")
                .multiPart("avatar", "temp.jpg", "hello world2".getBytes(), "image/jpeg")
                .put("/customer/100/partnermapping/102/avatar")
                .then().assertThat()
                .statusCode(200);
    }

    @Test
    public void getAvatar() throws Exception {
        given().header("authorization", "Bearer 666666666")
                .get("/customer/100/partnermapping/102/avatar")
                .then().assertThat()
                .statusCode(200)
                .body("id", equalTo(104))
                .body("mimetype", equalTo("image/jpeg"))
                .body("photo", notNullValue());
    }

    @Test
    public void getCurrentCustomerAvatar() throws Exception {
        given().header("authorization", "Bearer 666666666")
                .get("/customer/@me/partnermapping/102/avatar")
                .then().assertThat()
                .statusCode(200)
                .body("id", equalTo(104))
                .body("mimetype", equalTo("image/jpeg"))
                .body("photo", notNullValue());
    }

    @Test
    public void deleteAvatar() throws Exception {
        given().header("authorization", "Bearer 666666666")
                .delete("/customer/100/partnermapping/102/avatar")
                .then().assertThat()
                .statusCode(200);
    }

    @Test
    public void deleteCurrentCustomerAvatar() throws Exception {

        given().header("authorization", "Bearer 666666666")
                .delete("/customer/@me/partnermapping/102/avatar")
                .then().assertThat()
                .statusCode(200);
    }

}