package com.group4.softwareanalytics;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.util.io.DisabledOutputStream;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CommitExtractor {
    public static void DownloadRepo(String url, String destUrl)
    {
        String repoUrl = url;
        String cloneDirectoryPath = destUrl;
        try {
            System.out.println("Cloning "+repoUrl+" into "+repoUrl);
            Git.cloneRepository()
                    .setURI(repoUrl)
                    .setDirectory(Paths.get(cloneDirectoryPath).toFile())
                    .call();
            System.out.println("Completed Cloning");
        } catch (GitAPIException e) {
            System.out.println("Exception occurred while cloning repo");
            e.printStackTrace();
        }
    }


    public static List<RevCommit> getCommits(String branchName, Git git, Repository repo) throws IOException, GitAPIException {
        List<RevCommit> commitList = new ArrayList<>();
        for (RevCommit commit : git.log().add(repo.resolve(branchName)).call()) {
            commitList.add(commit);
        }
        return commitList;
    }

    public static List<DiffEntry> getModifications(Git git, String commitID) throws IOException, GitAPIException {

        List<DiffEntry> entriesList = new ArrayList<>();
        try {
            ObjectReader reader = git.getRepository().newObjectReader();
            CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
            ObjectId oldTree = git.getRepository().resolve(commitID + "~1^{tree}");
            oldTreeIter.reset(reader, oldTree);
            CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
            ObjectId newTree = git.getRepository().resolve(commitID + "^{tree}");
            newTreeIter.reset(reader, newTree);

            DiffFormatter diffFormatter = new DiffFormatter(DisabledOutputStream.INSTANCE);
            diffFormatter.setRepository(git.getRepository());
            List<DiffEntry> entries = diffFormatter.scan(oldTreeIter, newTreeIter);
            for (DiffEntry entry : entries) {
                entriesList.add(entry);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return entriesList;
    }

}
