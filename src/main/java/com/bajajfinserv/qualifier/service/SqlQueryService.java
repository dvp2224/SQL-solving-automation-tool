package com.bajajfinserv.qualifier.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SqlQueryService {
    
    private static final Logger logger = LoggerFactory.getLogger(SqlQueryService.class);
    
    /**
     * Determines the SQL query based on registration number
     * Odd regNo -> Question 1, Even regNo -> Question 2
     */
    public String generateSqlQuery(String regNo) {
        logger.info("Generating SQL query for regNo: {}", regNo);
        
        try {
            // Extract last two digits from regNo
            String lastTwoDigits = regNo.substring(regNo.length() - 2);
            int lastTwoDigitsInt = Integer.parseInt(lastTwoDigits);
            
            logger.info("Last two digits: {}", lastTwoDigitsInt);
            
            if (lastTwoDigitsInt % 2 == 1) {
                // Odd - Question 1
                logger.info("Registration number is odd, using Question 1 SQL");
                return getQuestion1SqlQuery();
            } else {
                // Even - Question 2
                logger.info("Registration number is even, using Question 2 SQL");
                return getQuestion2SqlQuery();
            }
        } catch (Exception e) {
            logger.error("Error processing regNo: {}", regNo, e);
            // Default to Question 1 if there's an error
            return getQuestion1SqlQuery();
        }
    }
    
    /**
     * SQL Query for Question 1 (Odd regNo)
     * Common SQL pattern for finding top N records with aggregations
     */
    private String getQuestion1SqlQuery() {
        return """
            SELECT 
                department_id,
                department_name,
                AVG(salary) as avg_salary,
                COUNT(*) as employee_count
            FROM employees e
            INNER JOIN departments d ON e.department_id = d.id
            WHERE e.status = 'ACTIVE'
            GROUP BY department_id, department_name
            HAVING COUNT(*) > 5
            ORDER BY avg_salary DESC
            LIMIT 10;
            """;
    }
    
    /**
     * SQL Query for Question 2 (Even regNo)
     * Common SQL pattern for hierarchical data and ranking
     */
    private String getQuestion2SqlQuery() {
        return """
            WITH RankedEmployees AS (
                SELECT 
                    e.id,
                    e.name,
                    e.salary,
                    e.department_id,
                    e.manager_id,
                    ROW_NUMBER() OVER (PARTITION BY e.department_id ORDER BY e.salary DESC) as rank
                FROM employees e
                WHERE e.hire_date >= DATE_SUB(CURDATE(), INTERVAL 2 YEAR)
            )
            SELECT 
                re.id,
                re.name,
                re.salary,
                d.department_name,
                m.name as manager_name
            FROM RankedEmployees re
            INNER JOIN departments d ON re.department_id = d.id
            LEFT JOIN employees m ON re.manager_id = m.id
            WHERE re.rank <= 3
            ORDER BY d.department_name, re.rank;
            """;
    }
}