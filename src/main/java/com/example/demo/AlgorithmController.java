package com.example.demo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ca.pfv.spmf.algorithms.frequentpatterns.zart.AlgoZart;
import ca.pfv.spmf.algorithms.frequentpatterns.zart.TFTableFrequent;
import ca.pfv.spmf.algorithms.frequentpatterns.zart.TZTableClosed;
import ca.pfv.spmf.input.transaction_database_list_integers.TransactionDatabase;
import ca.pfv.spmf.patterns.itemset_array_integers_with_count.Itemset;

@RestController
@RequestMapping("/api")
public class AlgorithmController {
	@RequestMapping(value = "/test/", method = RequestMethod.POST)
	public Object test(@Validated @RequestBody String transactions) throws IOException, ParseException {
		// *********************************************************
		String hama = "";
		String result = "";
		// String file =
		// "/home/amine/eclipse-workspace/demo/src/main/java/ca/pfv/spmf/test/hama.txt";

		System.out.println("***********************Transactions : " + transactions);
		JSONParser parser = new JSONParser();
		JSONObject jsonSt = (JSONObject) parser.parse(transactions);
		JSONObject jsonRoot = new JSONObject(jsonSt);
		ArrayList str_name = (ArrayList) jsonRoot.get("postcodes");

		// System.out.println("**********************str_name : "+str_name.get(1));
		/* System.out.print("hama"+array2); */
		for (int l = 0; l < str_name.size(); l++) {
			hama += str_name.get(l).toString() + "\n";

			System.out.println(hama);
		}

		/*
		 * Integer str_id = Integer.valueOf(jsonRoot.get("id")); List<String> list = new
		 * ArrayList<String>(); JSONArray jsonList = jsonRoot.getJSONArray("test"); /*
		 * for (int i = 0; i < jsonList.length(); i++) {
		 * list.add(jsonList.getString(i)); }
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * /*for(int i=0; i<zoneList.size(); i++) { long p = (Long)zoneList.get(i);
		 * System.out.print(p+", "); } System.out.println();
		 * 
		 * JSONArray weekdayList = (JSONArray) jsonObject.get("Weekdays");
		 * System.out.print("\nWeekday List: "); System.out.println(weekdayList.size());
		 * 
		 * for(int i=0; i<weekdayList.size(); i++) { boolean p =
		 * (boolean)weekdayList.get(i); System.out.print(p+", "); }
		 * System.out.println();
		 * 
		 * } catch (Exception e) { e.printStackTrace(); } } }
		 */

		// System.out.println(obj);

		/*
		 * for (int i = 0; i < transactions.size(); i++) { klil=
		 * transactions.get("postcodes");
		 * 
		 * } JSONArray arr = new JSONArray(); arr.put(klil); System.out.println(arr);
		 * 
		 * /*System.out.print("hama"+array2); for(int i=0;i<transactions.size();i++) {
		 * hama+=transactions.get(i).toString()+"\n"; }
		 */
		// System.out.println(hama);
		FileOutputStream fop = null;
		File file;

		try {

			file = new File("px.txt");
			fop = new FileOutputStream(file);

			// if file doesnt exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			// get the content in bytes
			byte[] contentInBytes = hama.getBytes();

			fop.write(contentInBytes);
			fop.flush();
			fop.close();

			System.out.println("Done");

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fop != null) {
					fop.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// Load a binary context
		TransactionDatabase context = new TransactionDatabase();
		context.loadFile("px.txt");//
		// Apply the Zart algorithm
		double minsup = 0.4;
		AlgoZart zart = new AlgoZart();
		TZTableClosed results = zart.runAlgorithm(context, minsup);
		TFTableFrequent frequents = zart.getTableFrequent();
		zart.printStatistics();

		// PRINTING RESULTS
		int countClosed = 0;
		int countGenerators = 0;
		result = result + "***********************les Transactions sont  : \n" + transactions + "\n";
		result = result + "======= List of closed itemsets and their generators ============ : \n ";
		System.out.println("======= List of closed itemsets and their generators ============");
		for (int i = 0; i < results.levels.size(); i++) {
			result = result + "LEVEL (SIZE) : " + i + "\n";
			System.out.println("LEVEL (SIZE) : " + i);
			for (Itemset closed : results.levels.get(i)) {
				result = result + "CLOSED :" + closed.toString() + "  supp : " + closed.getAbsoluteSupport();
				System.out.println(" CLOSED : \n   " + closed.toString() + "  supp : " + closed.getAbsoluteSupport());
				countClosed++;
				result = result + " GENERATORS : : \n ";
				System.out.println("   GENERATORS : ");

				List<Itemset> generators = results.mapGenerators.get(closed);
				// if there are some generators
				if (generators.size() != 0) {
					for (Itemset generator : generators) {
						countGenerators++;
						result = result + "  =" + generator.toString() + "\n";
						System.out.println("     =" + generator.toString());
					}
				} else {
					// otherwise the closed itemset is a generator
					countGenerators++;
					result = result + "  =" + closed.toString();
					System.out.println("     =" + closed.toString());
				}
			}
			result = result + " NUMBER OF CLOSED : " + countClosed + " NUMBER OF GENERATORS : " + countGenerators;

		}
		System.out.println(" NUMBER OF CLOSED : " + countClosed + " NUMBER OF GENERATORS : " + countGenerators);

		// SECOND, WE PRINT THE LIST OF ALL FREQUENT ITEMSETS
		result = result + "======= List of all frequent itemsets ============ \n";
		System.out.println("======= List of all frequent itemsets ============");
		int countFrequent = 0;
		for (int i = 0; i < frequents.levels.size(); i++) {
			result = result + "LEVEL (SIZE) :" + i + "\n";
			System.out.println("LEVEL (SIZE) : " + i);
			for (Itemset itemset : frequents.levels.get(i)) {
				countFrequent++;
				result = result + " ITEMSET : " + itemset.toString() + "  supp : " + itemset.getAbsoluteSupport()
						+ "\n";
				System.out.println(" ITEMSET : " + itemset.toString() + "  supp : " + itemset.getAbsoluteSupport());
			}
		}
		result = result + "NB OF FREQUENT ITEMSETS : " + countFrequent + "\n";
		System.out.println("NB OF FREQUENT ITEMSETS : " + countFrequent);

		return (result);
	}

	@RequestMapping(value = "/sum", method = RequestMethod.POST)
	public int calculateSum(@RequestParam("num1") int number1, @RequestParam("num2") int number2) {
		return number1 + number2;
	}

}
