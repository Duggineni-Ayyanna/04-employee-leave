package com.example.leave;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/leaves")
public class LeaveController {

    private final List<Leave> leaves = new ArrayList<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    // Apply for leave
    @PostMapping
    public ResponseEntity<Leave> applyLeave(@Valid @RequestBody Leave leave) {
        leave.setEmployeeId(idCounter.getAndIncrement());
        leaves.add(leave);
        return ResponseEntity.status(HttpStatus.CREATED).body(leave);
    }

    // Get all leave requests
    @GetMapping
    public List<Leave> getAllLeaves() {
        return leaves;
    }

    // Get leave by employee id
    @GetMapping("/{id}")
    public ResponseEntity<Leave> getLeaveById(@PathVariable Long id) {
        return leaves.stream()
                .filter(l -> l.getEmployeeId().equals(id))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Update leave request
    @PutMapping("/{id}")
    public ResponseEntity<Leave> updateLeave(@PathVariable Long id, @Valid @RequestBody Leave updatedLeave) {
        for (Leave leave : leaves) {
            if (leave.getEmployeeId().equals(id)) {
                leave.setEmployeeName(updatedLeave.getEmployeeName());
                leave.setLeaveType(updatedLeave.getLeaveType());
                leave.setNumberOfDays(updatedLeave.getNumberOfDays());
                leave.setReason(updatedLeave.getReason());
                return ResponseEntity.ok(leave);
            }
        }
        return ResponseEntity.notFound().build();
    }

    // Delete leave request
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteLeave(@PathVariable Long id) {
        boolean removed = leaves.removeIf(l -> l.getEmployeeId().equals(id));
        if (removed) {
            return ResponseEntity.ok("Leave request deleted successfully");
        }
        return ResponseEntity.notFound().build();
    }
}
