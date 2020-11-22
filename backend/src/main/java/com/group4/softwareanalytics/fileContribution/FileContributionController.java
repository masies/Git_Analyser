package com.group4.softwareanalytics.fileContribution;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

@RestController
@RequestMapping(path = "/api/", produces = MediaType.APPLICATION_JSON_VALUE)
public class FileContributionController {

    @Autowired
    private  FileContributionRepository fileContributionRepository;

    @RequestMapping(value = "/repo/{owner}/{repo}/fileContributions", method = {RequestMethod.GET})
    public @ResponseBody
    ArrayList<FileContribution> getAttr(
            @PathVariable(value="owner") String owner,
            @PathVariable(value="repo") String repo) {
        return fileContributionRepository.findByOwnerAndRepo(owner, repo);
    }

    @RequestMapping("/repo/{owner}/{repo}/fileContributions/**")
    public ArrayList<FileContribution> foo(@PathVariable(value="owner") String owner,
                    @PathVariable(value="repo") String repo,
                    HttpServletRequest request) {
        String restOfTheUrl = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String path = restOfTheUrl.replace("/api/repo/"+ owner +"/"+ repo +"/fileContributions/","") + "/";
        ArrayList<FileContribution> fileContributions = fileContributionRepository.findByOwnerAndRepo(owner,repo);

        ArrayList<FileContribution> requestedLevelContributions = new ArrayList<>();

        for (FileContribution fileContribution: fileContributions) {
            if (fileContribution.getPath().contains(path)){
                if (!fileContribution.getPath().replace(path,"").contains("/")){
                    System.out.println(fileContribution.getPath().replace(path,""));
                    requestedLevelContributions.add(fileContribution);
                }
            }
        }

        return requestedLevelContributions;



    }
}
