import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class DecimalTest {

	@Test
	void test() {

		for (int i = 0; i < 1000; i++) {
			for (int k = 0; k < 1000; k++) {
				Decimal a = new Decimal("" + i);
				Decimal b = new Decimal("" + k);
				Decimal c = Decimal.sum(a, b);
				String err = "Addition error when " + a.getFormat() + " sums with " + b.getFormat();
				assertTrue(Double.parseDouble(c.getFormat()) == i + k, err);
			}
		}	
	}

}
