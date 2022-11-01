package com.brave.inter.process.server.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.brave.inter.process.server.DaoHelper
import com.brave.inter.process.server.entity.IStudentService
import com.brave.inter.process.server.entity.StudentEntity

class StudentService : Service() {
    override fun onBind(p0: Intent?): IBinder = stub

    private val stub = object : IStudentService.Stub() {
        override fun queryAll(): MutableList<StudentEntity> {
            return DaoHelper.daoSession.studentEntityDao
                .queryBuilder()
                .list()
        }

        override fun addStudentInOut(student: StudentEntity?) {
            if (null == student) return
            student.name = "${student.name}(inout)"
            DaoHelper.daoSession.insert(student)
        }

        override fun addStudentIn(student: StudentEntity?) {
            if (null == student) return
            student.name = "${student.name}(in)"
            DaoHelper.daoSession.insert(student)
        }

        override fun addStudentOut(student: StudentEntity?) {
            if (null == student) return
            student.name = "${student.name}(out)"
            DaoHelper.daoSession.insert(student)
        }
    }
}