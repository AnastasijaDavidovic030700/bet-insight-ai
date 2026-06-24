package com.betinsight.service;

import com.betinsight.entity.Branch;
import com.betinsight.repository.BranchRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BranchService {

    private final BranchRepository branchRepository;

    public BranchService(BranchRepository branchRepository) {
        this.branchRepository = branchRepository;
    }

    public List<Branch> getAllBranches() {
        return branchRepository.findAll();
    }

    public Branch getBranchById(Long id) {
        return branchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Branch not found with id: " + id));
    }

    public Branch createBranch(Branch branch) {
        branch.setId(null);

        if (branch.getActive() == null) {
            branch.setActive(true);
        }

        return branchRepository.save(branch);
    }

    public Branch updateBranch(Long id, Branch updatedBranch) {
        Branch existingBranch = getBranchById(id);

        existingBranch.setName(updatedBranch.getName());
        existingBranch.setCity(updatedBranch.getCity());
        existingBranch.setAddress(updatedBranch.getAddress());

        if (updatedBranch.getActive() != null) {
            existingBranch.setActive(updatedBranch.getActive());
        }

        return branchRepository.save(existingBranch);
    }

    public void deleteBranch(Long id) {
        Branch existingBranch = getBranchById(id);
        branchRepository.delete(existingBranch);
    }
}