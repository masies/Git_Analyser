package com.group4.softwareanalytics.commits;

import com.github.mauricioaniche.ck.CK;
import com.group4.softwareanalytics.metrics.MetricResults;
import com.group4.softwareanalytics.metrics.ProjectMetric;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.*;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;

public class CommitExtractor {

    public static void DownloadRepo(String url, String owner, String repoName) {
        String path = "./repo/" + owner +"/"+ repoName;
        try {
            System.out.println("Cloning "+ url +" into "+ path);
            Git.cloneRepository()
                    .setURI(url)
                    .setDirectory(Paths.get(path).toFile())
                    .call();
            System.out.println("------- Repo cloned succesfully! -------");

        } catch (GitAPIException e){
            Logger logger = LogManager.getLogger(CommitExtractor.class.getName());
            logger.error(e.getMessage(),e);
        }
    }

    public static List<RevCommit> getCommits(String branchName, Git git, Repository repo) throws IOException, GitAPIException {
        List<RevCommit> commitList = new ArrayList<>();
        for (RevCommit commit : git.log().add(repo.resolve(branchName)).call()) {
            commitList.add(commit);
        }
        return commitList;
    }

    public static List<CommitDiff> getModifications(Git git, String commit, String path, ArrayList<String> commitParentsIDs) {
        List<CommitDiff> entriesList = new ArrayList<>();
        try {
            CanonicalTreeParser oldTreeIter;
            CanonicalTreeParser newTreeIter;
            try (ObjectReader reader = git.getRepository().newObjectReader()) {
                oldTreeIter = new CanonicalTreeParser();
                ObjectId oldTree = git.getRepository().resolve(commit + "~1^{tree}");
                oldTreeIter.reset(reader, oldTree);
                newTreeIter = new CanonicalTreeParser();
                ObjectId newTree = git.getRepository().resolve(commit + "^{tree}");
                newTreeIter.reset(reader, newTree);


                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                DiffFormatter diffFormatter = new DiffFormatter(stream);

                diffFormatter.setRepository(git.getRepository());
                for (DiffEntry entry : diffFormatter.scan(oldTreeIter, newTreeIter)) {
                    diffFormatter.format(entry);
                    String diffText = stream.toString();
                    ArrayList<Float> currentMetrics = new ArrayList<>();
                    ArrayList<Float> parentMetrics = new ArrayList<>();

                    if (entry.getChangeType().name().equals("MODIFY")) {

                        String repoNewPath = path + "/" + entry.getNewPath();
                        ;
                        String repoOldPath = path + "/" + entry.getOldPath();
                        ;
                        MetricResults results = new MetricResults();
                        CK ck = new CK();
                        ck.calculate(repoNewPath, results);
                        currentMetrics = results.getResults();

                        if (commitParentsIDs.size() == 1) {
                            results = new MetricResults();
                            git.checkout().setName(commitParentsIDs.get(0)).call();
                            ck.calculate(repoOldPath, results);
                            parentMetrics = results.getResults();
                            git.checkout().setName(commit).call();
                        }
                    }

                    ProjectMetric pm = new ProjectMetric(currentMetrics.get(0), currentMetrics.get(1), currentMetrics.get(2), currentMetrics.get(3), parentMetrics.get(0), parentMetrics.get(1), parentMetrics.get(2), parentMetrics.get(3));

                    CommitDiff cd = new CommitDiff(entry.getOldPath(), entry.getNewPath(), entry.getChangeType().name(), diffText, pm);

                    entriesList.add(cd);
                    stream.reset();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        catch (Exception ignore) {
            /* commits with no modification */
        } 
        return entriesList;
    }
}
