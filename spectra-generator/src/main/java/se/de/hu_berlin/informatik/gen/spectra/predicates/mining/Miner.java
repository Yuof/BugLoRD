package se.de.hu_berlin.informatik.gen.spectra.predicates.mining;

import com.google.common.primitives.Ints;
import org.apache.commons.lang3.tuple.Pair;
import se.de.hu_berlin.informatik.gen.spectra.predicates.extras.Profile;
import se.de.hu_berlin.informatik.utils.miscellaneous.Log;
import se.de.hu_berlin.informatik.utils.miscellaneous.Misc;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.*;

public class Miner {

    public HashMap<Signature.Identifier, Signature> mine(String folder) {

        String row;
        Database db = new Database();

        Log.out(Miner.class, "Mining %s.", folder);

        //reset file
        try {
            new FileOutputStream(Paths.get(folder, "Signatures.csv").toFile()).close();
        }
        catch (IOException ex) {
            Log.abort(Miner.class,"could not reset Signatures.csv");
        }

        Log.out(this, "Setting up first DB.");
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

        int neg_support = (int) Math.ceil(db.getNegativeCount() / 2.0);

        db.PurgeFullSupport();

        //Log.out(this, MessageFormat.format("Starting Mining with {0} Items in DB", db.transactions.size()));
        long startTime = new Date().getTime();
        GrTreeMiner miner = new GrTreeMiner(db);
        HashMap<Pair<Integer, Integer>, HashSet<GrTree.Item>> generators = miner.MineSignatures(3,neg_support,3);

        Log.out(this, "Mining time: %s",  Misc.getFormattedTimerString(new Date().getTime() - startTime));
        Log.out(this, "Finish Mining.");
        List<Map.Entry<Pair<Integer, Integer>, HashSet<GrTree.Item>>> sorted = new ArrayList<>(generators.entrySet());
        sorted.sort(Comparator.comparingDouble(entry -> miner.DiscriminativeSignificance(entry.getKey().getLeft(), entry.getKey().getRight())));
        Collections.reverse(sorted);
        Log.out(this, "Creating Signatures.");
        HashMap<Signature.Identifier, Signature> signatures = new LinkedHashMap<>();
        try {
            sorted.forEach(entry -> {
                entry.getValue().forEach(item -> {
                    Signature.Identifier newIdent = new Signature.Identifier(entry.getKey().getLeft(), entry.getKey().getRight(), db, miner, item);
                    Signature sig = signatures.get(newIdent);
                    if (sig == null) {
                        sig = new Signature(newIdent);
                    }

                    sig.allItems.add(item);

                    signatures.put(newIdent, sig);
                });
                if (signatures.size() >= 10) throw new SizeLimitException();
            });
        }
        catch (SizeLimitException ignored) {
            //limit to 10 result entries
        }



        return signatures;


//        System.out.println();
//        sorted.forEach(entry -> System.out.println("DS: " + miner.DiscriminativeSignificance(entry.getKey().getLeft(),entry.getKey().getRight())
//                + "; "
//                + "Support: ( +" + entry.getKey().getLeft() + ", -" + entry.getKey().getRight()
//                + " ); "
//                + Arrays.toString(entry.getValue().stream().map(item -> item.prefixedId).toArray())));
//
//        sorted.forEach(entry -> writeSignatures("DS: " + miner.DiscriminativeSignificance(entry.getKey().getLeft(),entry.getKey().getRight())
//                + "; "
//                + "Support: ( +" + entry.getKey().getLeft() + ", -" + entry.getKey().getRight()
//                + " ); "
//                + Arrays.toString(entry.getValue().stream().map(item -> item.prefixedId).toArray()), folder));


    }


    private static class SizeLimitException extends RuntimeException{
    }
}