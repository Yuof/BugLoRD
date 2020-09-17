package se.de.hu_berlin.informatik.gen.spectra.predicates.mining;

import com.google.common.primitives.Ints;
import org.apache.commons.lang3.tuple.Pair;
import se.de.hu_berlin.informatik.gen.spectra.predicates.extras.Profile;
import se.de.hu_berlin.informatik.utils.miscellaneous.Log;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;

public class Miner {

    public static void main(String[] args) {

        String folder = args[0];
        String row;
        Database db = new Database();

        //reset file
        try {
            new FileOutputStream(Paths.get(folder, "Signatures.csv").toFile()).close();
        }
        catch (IOException ex) {
            Log.abort(Miner.class,"could not reset Signatures.csv");
        }

        try {
            BufferedReader csvReader = new BufferedReader(new FileReader(Paths.get(folder, "Profiles.csv").toString()));
            while ((row = csvReader.readLine()) != null) {
                String[] profileString = row.split(";");
                List<Integer> predicates = new ArrayList<>();
                if (!profileString[0].isEmpty()) {
                    predicates = Ints.asList(Arrays.stream(profileString[0].split(",")).mapToInt(Integer::parseInt).toArray());
                }
                Profile profile = new Profile(predicates,Boolean.parseBoolean(profileString[1]));
                db.addProfile(profile);
            }
            csvReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

//        Profile t1 = new Profile(Arrays.asList(2), true);
//        Profile t2 = new Profile(Arrays.asList(4,12), true);
//        Profile t3 = new Profile(Arrays.asList(4,7,9,14,11), true);
//        Profile t4 = new Profile(Arrays.asList(4,7,9,14,16), true);
//        Profile t5 = new Profile(Arrays.asList(4,7,9,11,16), false);
//        Database db = new Database(new ArrayList<Profile>(Arrays.asList(t1, t2, t3, t4, t5)),new ArrayList<>());

        //int neg_support = db.getNegativeCount() / 2.0 ;
        db.PurgeFullSupport();

        GrTreeMiner miner = new GrTreeMiner();
        HashMap<Pair<Integer, Integer>, HashSet<GrTree.Item>> signatures = miner.MineSignatures(db,10,1,2);


        List<Map.Entry<Pair<Integer, Integer>, HashSet<GrTree.Item>>> sorted = new ArrayList<>(signatures.entrySet());
        sorted.sort(Comparator.comparingDouble(entry -> miner.DiscriminativeSignificance(entry.getKey().getLeft(), entry.getKey().getRight(), db.getPositiveCount(), db.getNegativeCount())));
        Collections.reverse(sorted);

        System.out.println();
        sorted.forEach(entry -> System.out.println("DS: " + miner.DiscriminativeSignificance(entry.getKey().getLeft(),entry.getKey().getRight(),db.getPositiveCount(),db.getNegativeCount())
                + "; "
                + "Support: ( +" + entry.getKey().getLeft() + ", -" + entry.getKey().getRight()
                + " ); "
                + Arrays.toString(entry.getValue().stream().map(item -> item.prefixedId).toArray())));

        sorted.forEach(entry -> writeSignatures("DS: " + miner.DiscriminativeSignificance(entry.getKey().getLeft(),entry.getKey().getRight(),db.getPositiveCount(),db.getNegativeCount())
                + "; "
                + "Support: ( +" + entry.getKey().getLeft() + ", -" + entry.getKey().getRight()
                + " ); "
                + Arrays.toString(entry.getValue().stream().map(item -> item.prefixedId).toArray()), folder));

    }

    static void writeSignatures(String line, String fileString) {
        try {
            FileWriter fw = new FileWriter(Paths.get(fileString, "Signatures.csv").toString(),true);
            fw.append(line);
            fw.append(System.lineSeparator());
            fw.flush();
            fw.close();
        }
        catch (IOException ex) {
            System.out.println("IOException while writing to file");
        }
    }
}
