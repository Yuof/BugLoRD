package se.de.hu_berlin.informatik.aspectj.frontend;

import org.apache.commons.cli.Option;
import org.jdom.JDOMException;
import se.de.hu_berlin.informatik.aspectj.frontend.evaluation.sbfl.CreateRankingsFromSpectra;
import se.de.hu_berlin.informatik.utils.optionparser.OptionParser;
import se.de.hu_berlin.informatik.utils.optionparser.OptionWrapper;
import se.de.hu_berlin.informatik.utils.optionparser.OptionWrapperInterface;

import java.io.IOException;


/**
 * @author Simon Heiden
 */
public class ExperimentRunner {

    public enum CmdOptions implements OptionWrapperInterface {
        /* add options here according to your needs */
//      options.add(Option.builder(Prop.OPT_PROJECT).longOpt("projects").required().hasArgs()
//		.desc("A list of projects to consider of the Defects4J benchmark. "
//		+ "Should be either 'Lang', 'Chart', 'Time', 'Closure' or 'Math'.").build());
//options.add(Option.builder(Prop.OPT_BUG_ID).longOpt("bugIDs").required().hasArgs()
//		.desc("A list of numbers indicating the ids of buggy project versions to consider. "
//		+ "Value ranges differ based on the project. Set this to 'all' to "
//		+ "iterate over all bugs in a project.").build());
//
//
//options.add(Option.builder(Prop.OPT_LOCALIZERS).longOpt("localizers").optionalArg(true).hasArgs()
//		.desc("A list of identifiers of Cobertura localizers (e.g. 'Tarantula', 'Jaccard', ...).")
//		.build());
        ;

        /* the following code blocks should not need to be changed */
        final private OptionWrapper option;

        //adds an option that is not part of any group
        CmdOptions(final String opt, final String longOpt,
                   final boolean hasArg, final String description, final boolean required) {
            this.option = new OptionWrapper(
                    Option.builder(opt).longOpt(longOpt).required(required).
                            hasArg(hasArg).desc(description).build(), NO_GROUP);
        }

        //adds an option that is part of the group with the specified index (positive integer)
        //a negative index means that this option is part of no group
        //this option will not be required, however, the group itself will be
        CmdOptions(final String opt, final String longOpt,
                   final boolean hasArg, final String description, int groupId) {
            this.option = new OptionWrapper(
                    Option.builder(opt).longOpt(longOpt).required(false).
                            hasArg(hasArg).desc(description).build(), groupId);
        }

        //adds the given option that will be part of the group with the given id
        CmdOptions(Option option, int groupId) {
            this.option = new OptionWrapper(option, groupId);
        }

        //adds the given option that will be part of no group
        CmdOptions(Option option) {
            this(option, NO_GROUP);
        }

        @Override
        public String toString() {
            return option.getOption().getOpt();
        }

        @Override
        public OptionWrapper getOptionWrapper() {
            return option;
        }
    }

    /**
     * @param args command line arguments
     */
    public static void main(String[] args) {

        OptionParser options = OptionParser.getOptions("ExperimentRunner", true, CmdOptions.class, args);

//		String[] projects = options.getOptionValues(Prop.OPT_PROJECT);
//		String[] ids = options.getOptionValues(Prop.OPT_BUG_ID);
//		String[] localizers = options.getOptionValues(Prop.OPT_LOCALIZERS);
//		boolean all = ids[0].equals("all");

        int threadCount = options.getNumberOfThreads();

        try {
            new CreateRankingsFromSpectra(threadCount, false).run();
        } catch (IOException | JDOMException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


}
