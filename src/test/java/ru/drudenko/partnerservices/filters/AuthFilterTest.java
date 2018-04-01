package ru.drudenko.partnerservices.filters;

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

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AuthFilterTest {
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
    public void authGood() throws Exception {
        given().header("authorization", "Bearer 666666666")
                .get("/customer/100")
                .then().assertThat()
                .statusCode(200);
    }

    @Test
    public void authForbidden() throws Exception {
        given().header("authorization", "Bearer 666666666")
                .get("/customer/101")
                .then().assertThat()
                .statusCode(403);
    }

    @Test
    public void authUnauthorizedTokenNotFound() throws Exception {
        given().header("authorization", "Bearer 1111")
                .get("/customer/100")
                .then().assertThat()
                .statusCode(401);
    }

    @Test
    public void authUnauthorizedTokenExpired() throws Exception {
        given().header("authorization", "Bearer 777777777")
                .get("/customer/101")
                .then().assertThat()
                .statusCode(401);
    }

    @Test
    public void authBadRequest() throws Exception {
        given()
                .get("/customer/100")
                .then().assertThat()
                .statusCode(400);
    }
}