package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.anno.AutoFill;
import com.sky.entity.Employee;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

//@Mapper
public interface EmployeeMapper {

    /**
     * 根据用户名查询员工
     * @param username
     * @return
     */
    @Select("select * from employee where username = #{username}")
    Employee getByUsername(String username);
    //新增员工
    @AutoFill(OperationType.INSERT)
    @Insert("insert into employee values (null,#{name}, #{username}, #{password}, #{phone}, #{sex}, #{idNumber}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser}) ")
    void insert(Employee employee);
    // 查询员工
    Page<Employee> list(String name);
    //修改员工
    @AutoFill(OperationType.UPDATE)
    void update(Employee employee);
    @Select("select * from employee where id = #{id}")
    Employee getById(Long id);
}
