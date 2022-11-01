package com.brave.inter.process.server.entity;

import com.brave.inter.process.server.entity.StudentEntity;

interface IStudentService {
    // query
    List<StudentEntity> queryAll();

    // add by inout
    void addStudentInOut(inout StudentEntity student);

    // add by in
    void addStudentIn(in StudentEntity student);

    // add by out
    void addStudentOut(out StudentEntity student);
}