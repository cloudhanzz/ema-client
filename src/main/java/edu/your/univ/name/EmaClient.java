package edu.your.univ.name;

import han.jia.cloud.nlp.ema.MorphNode;
import han.jia.cloud.nlp.ema.MorphParser;
import han.jia.cloud.nlp.enums.MorphType;
import han.jia.cloud.nlp.service.WordService;
import han.jia.cloud.nlp.util.SpringAppContextCreator;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.function.Predicate;

import org.springframework.context.ApplicationContext;

/**
 * Example class demonstrating how to use the ema library.
 * 
 * @author Jiayun Han
 *
 */
public class EmaClient {

	private static final String formatter = "  %s (%s)";

	private static void printStrings(List<String> ss, String name) {
		System.out.println("\n" + name + ": ");
		ss.stream().forEach(System.out::println);
	}

	public static void main(String[] args) throws ClassNotFoundException, Exception {
			
		// Get the spring application context
		ApplicationContext context = SpringAppContextCreator.getAppContext();

		// From the application context, get the WordService bean
		WordService wordService = context.getBean(WordService.class);

		// Use WordService bean to create a MorphParser and we are all set!
		MorphParser parser = new MorphParser(wordService);

		System.out.println("Enter a word to parse it. or \\q to quit");

		String word;
		try (BufferedReader inputReader = new BufferedReader(new InputStreamReader(System.in))) {

			while ((word = inputReader.readLine()) != null) {

				if (word.equals("\\q")) {
					System.out.println("Bye!");
					System.exit(0);
				}

				MorphNode node = parser.parse(word);

				if (node != null) {

					// pretty-print this word morphologically
					System.out.println();
					System.out.println(node);

					// find suffixes
					List<String> suffixes = node.findSuffixes();
					printStrings(suffixes, "Suffixes");

					// find prefixes
					List<String> prefixes = node.findPrefixes();
					printStrings(prefixes, "Prefixes");

					// find content morphems
					List<String> contentMorphemes = node.findContentMorphemes();
					printStrings(contentMorphemes, "all content morphems");

					// find roots
					List<String> roots = node.findRoots();
					printStrings(roots, "all roots");

					// find, count, and print all morphemes, regardless types,
					List<MorphNode> morphemes = node.findAllMorphemes();

					System.out.println("\n" + morphemes.size() + " morphemes: ");
					morphemes.stream().map(n -> String.format(formatter, n.getText(), n.getType().name()))
							.forEach(System.out::println);

					// find inflection, this is the example of providing your
					// own predicate to filter the type of morphemes you are
					// interested in. Here, I want to find the inflection. You
					// can follow this example to find the stems as well.
					Predicate<MorphNode> myFilter = n -> n.getType() == MorphType.Inflection;
					List<String> inflections = node.findMorphemes(myFilter);					
					printStrings(inflections, "Inflections");
				}
			}
		}
	}
}
