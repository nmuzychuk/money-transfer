package com.nmuzychuk;

import org.junit.BeforeClass;
import org.junit.Test;
import spark.utils.IOUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import static org.junit.Assert.assertEquals;
import static spark.Spark.awaitInitialization;

/**
 * Unit test for simple App.
 */
public class AppTest {

    @BeforeClass
    public static void setup() {
        App.main(new String[0]);
        awaitInitialization();
    }

    @Test
    public void testAccounts() throws IOException {
        URL url = new URL("http://localhost:4567/accounts");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        String charset = "UTF-8";
        String s = "name=" + URLEncoder.encode("Bob", charset);
        s += "&balance=" + URLEncoder.encode("100", charset);

        conn.setFixedLengthStreamingMode(s.getBytes().length);
        PrintWriter out = new PrintWriter(conn.getOutputStream());
        out.print(s);
        out.close();

        String body = IOUtils.toString(conn.getInputStream());

        assertEquals(201, conn.getResponseCode());
        assertEquals("App.Account[id=1,name=Bob,balance=100]", body);

        url = new URL("http://localhost:4567/accounts");
        conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        s = "name=" + URLEncoder.encode("Kate", charset);
        s += "&balance=" + URLEncoder.encode("100", charset);

        conn.setFixedLengthStreamingMode(s.getBytes().length);
        out = new PrintWriter(conn.getOutputStream());
        out.print(s);
        out.close();

        body = IOUtils.toString(conn.getInputStream());

        assertEquals(201, conn.getResponseCode());
        assertEquals("App.Account[id=2,name=Kate,balance=100]", body);
    }

    @Test
    public void testTransfers() throws IOException {
        URL url = new URL("http://localhost:4567/transfers");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        String charset = "UTF-8";
        String s = "sender=" + URLEncoder.encode("1", charset);
        s += "&receiver=" + URLEncoder.encode("2", charset);
        s += "&amount=" + URLEncoder.encode("70", charset);

        conn.setFixedLengthStreamingMode(s.getBytes().length);
        PrintWriter out = new PrintWriter(conn.getOutputStream());
        out.print(s);
        out.close();

        String body = IOUtils.toString(conn.getInputStream());

        assertEquals(201, conn.getResponseCode());
        System.out.println(body);
        assertEquals("App.Transfer[id=1,status=New,sender=1,receiver=2,amount=70]", body);

        url = new URL("http://localhost:4567/transfers");
        conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

        s = "sender=" + URLEncoder.encode("1", charset);
        s += "&receiver=" + URLEncoder.encode("2", charset);
        s += "&amount=" + URLEncoder.encode("50", charset);

        conn.setFixedLengthStreamingMode(s.getBytes().length);
        out = new PrintWriter(conn.getOutputStream());
        out.print(s);
        out.close();

        body = IOUtils.toString(conn.getInputStream());

        assertEquals(201, conn.getResponseCode());
        System.out.println(body);
        assertEquals("App.Transfer[id=2,status=New,sender=1,receiver=2,amount=50]", body);
    }
}
