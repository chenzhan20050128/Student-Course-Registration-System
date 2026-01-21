<template>
  <div>
    <el-table :data="courses" style="width: 88%">
      <el-table-column prop="courseId" label="ID" width="75"></el-table-column>
      <el-table-column prop="courseName" label="课程名称" width="350"></el-table-column>
      <el-table-column prop="campus" label="上课地点" width="175"></el-table-column>
      <el-table-column prop="enrolledCount" label="选课人数" width="175"></el-table-column>
      <el-table-column prop="capacity" label="课程容量" width="150"></el-table-column>
      <el-table-column prop="college" label="所属专业" width="350"></el-table-column>
    </el-table>
    <el-pagination
      background
      layout="prev, pager, next"
      :total="total"
      :page-size="pageSize"
      @current-change="handlePageChange"
      class="custom-pagination"
      style="text-align: center; margin-top: 20px;"
    ></el-pagination>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import axios from '@/http'

export default {
  setup() {
    const courses = ref([])
    const total = ref(0)
    const pageSize = ref(10)
    const pageNum = ref(1)

    const fetchCourses = () => {
      axios.get('/teacher/listMyCourse', {
        params: {
          pageNum: pageNum.value,
          pageSize: pageSize.value,
        },
      }).then((response) => {
        if (response.data.code === 1) {
          const courseData = response.data.data.list.map(item => ({
            courseId: item.course.courseId,
            courseName: item.course.courseName,
            campus: item.course.campus,
            enrolledCount: item.course.enrolledCount,
            capacity: item.course.capacity,
            college: item.course.college,
          }))
          courses.value = courseData
          total.value = response.data.data.total
        } else {
          this.$message.error(response.data.msg || '获取课程信息失败')
        }
      })
    }

    const handlePageChange = (page) => {
      pageNum.value = page
      fetchCourses()
    }

    onMounted(() => {
      fetchCourses()
    })

    return {
      courses,
      total,
      pageSize,
      pageNum,
      handlePageChange,
    }
  }
}
</script>

<style scoped>
.custom-pagination {
  text-align: center;
  margin-top: 20px;
}
</style>