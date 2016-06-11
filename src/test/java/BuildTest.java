
import org.testng.annotations.*;
import static org.assertj.core.api.Assertions.*;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;
import org.json.simple.parser.JSONParser;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;

public class BuildTest {
	WebDriver driver;
	
	@BeforeClass
	public void Setup()
	{
//		driver = new org.openqa.selenium.htmlunit.HtmlUnitDriver();
		driver = new org.openqa.selenium.firefox.FirefoxDriver();
		driver.get("http://www.build.com");
	}
	
	@Test(dataProvider = "Test010")
	public void Test010CheckoutTotals(JSONObject data)
	{	
		for(Object i: (JSONArray)data.get("items")) 
		{
			JSONObject item = (JSONObject)i;
			addToCart((String)(item.get("name")), (String)(item.get("quantity")));
		}
		
		driver.findElement(By.className("cart-box")).click();
		driver.findElement(By.xpath("//div[@id='cartNav']/a")).click();
		driver.findElement(By.name("guestLoginSubmit")).click();
		
		driver.findElement(By.id("shippingfirstname")).sendKeys((String)(data.get("firstname")));
		driver.findElement(By.id("shippinglastname")).sendKeys((String)(data.get("lastname")));
		driver.findElement(By.id("shippingaddress1")).sendKeys((String)(data.get("address")));
		driver.findElement(By.id("shippingpostalcode")).sendKeys((String)(data.get("zip")));
		driver.findElement(By.id("shippingcity")).sendKeys((String)(data.get("city")));
		Select sel = new Select( driver.findElement(By.id("shippingstate_1")));
		sel.selectByVisibleText((String)(data.get("state")));
		driver.findElement(By.id("shippingphonenumber")).sendKeys((String)(data.get("phone")));
		driver.findElement(By.id("emailAddress")).sendKeys((String)(data.get("email")));
		driver.findElement(By.id("creditCardNumber")).sendKeys((String)(data.get("creditcard")));
		sel = new Select(driver.findElement(By.id("creditCardMonth")));
		sel.selectByVisibleText((String)(data.get("month")));
		sel = new Select(driver.findElement(By.id("creditCardYear")));
		sel.selectByVisibleText((String)(data.get("year")));
		driver.findElement(By.id("creditCardName")).sendKeys((String)(data.get("ccName")));
		driver.findElement(By.id("creditCardCVV2")).sendKeys((String)(data.get("CVV2")));
		
		driver.findElement(By.xpath("//input[@type='submit' and @value='Review Order']")).click();
		String subtotal = driver.findElement(By.id("subtotalamount")).getAttribute("data-subtotal");
		assertThat(subtotal).isEqualTo(data.get("subTotal"));
		String tax = driver.findElement(By.id("taxAmount")).getAttribute("data-tax");
		assertThat(tax).isEqualTo(Double.parseDouble((String)(data.get("taxRate"))) * Double.parseDouble(subtotal));
		String total = driver.findElement(By.id("grandtotalamount")).getText();
		assertThat(total).isEqualTo((String)(data.get("total")));
	}
	
	private void addToCart(String name, String quantity)
	{
		// Note, search box differs from page to page. The locators may not work on every page.
		driver.findElement(By.xpath("//form[@action='/index.cfm']//input[@name='term']")).sendKeys(name);
		driver.findElement(By.xpath("//form[@action='/index.cfm']//button[@type='submit']")).click();
		
		driver.findElement(By.id("qtyselected")).sendKeys(quantity.toString());
		driver.findElement(By.className("addToCart")).click();
		
	}
	
	@DataProvider(name = "Test010")
	public Object[][] Test010Data()
	{ // This should be replaced with a call to a real database, but that is tricky to deploy with just maven
		JSONParser parser = new JSONParser();
		String data = "{\r\n" + 
				"  \"items\": [\r\n" + 
				"    {\"name\":\"Suede Kohler K-­6626-­6U\", \"quantity\":\"1\"}\r\n" + 
				"    {\"name\":\"Cashmere Kohler K­-6626-­6U\", \"quantity\":\"1\"},\r\n" + 
				"    {\"name\":\"Kohler K­-6006­-ST\", \"quantity\":\"2\"},\r\n" + 	// Specified item, Kohler K-6066-ST, doesn't exist
				"  ],\r\n" + 											// assuming it is Kohler K-6006-ST. TODO double check the specs
				"  \"firstname\": \"Tester\",\r\n" + 
				"  \"lastname\": \"McTesterson\",\r\n" + 
				"  \"address\": \"123 main st\",\r\n" + 
				"  \"zip\": \"95926\",\r\n" + 
				"  \"city\":\"Chico\",\r\n" + 
				"  \"state\":\"California\",\r\n" + 
				"  \"phone\":\"(555)-555-1234\",\r\n" + 
				"  \"email\":\"TMcTesterson@example.com\",\r\n" + 
				"  \"creditcard\":\"4111111111111111\",\r\n" + 
				"  \"month\":\"11\",\r\n" + 
				"  \"year\":\"2026\",\r\n" + 
				"  \"ccName\":\"Bjarne T. Testerson\",\r\n" + 
				"  \"CVV2\":\"123\",\r\n" + 
				"  \"subTotal\":\"1,390.96\",\r\n" + 
				"  \"taxRate\":0.0750,\r\n" + 
				"  \"total\":\"$1,495.28\"\r\n" + 
				"}";
		Object[][] ret = new Object[1][1];
		try {
			ret[0][0] = parser.parse(data);
		} catch (ParseException pe) {
			System.out.println("position: " + pe.getPosition());
	        System.out.println(pe);
		}
		
		return ret;
	}
}
