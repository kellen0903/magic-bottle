<template>
  <el-dialog
    :title="'查看'"
    :close-on-click-modal="true"
    :visible.sync="visible">
    <el-form :model="dataForm" :disabled="true" ref="dataForm" label-width="80px">
      <el-form-item label="昵称" prop="nickName">
        <el-input v-model="dataForm.nickName" placeholder="昵称"></el-input>
      </el-form-item>
      <el-form-item label="星期" prop="week">
        <el-input v-model="dataForm.week" placeholder="星期"></el-input>
      </el-form-item>
      <el-form-item label="温度" prop="temperature">
        <el-input v-model="dataForm.temperature" placeholder="温度"></el-input>
      </el-form-item>
      <el-form-item label="天气" prop="weather">
        <el-input v-model="dataForm.weather" placeholder="天气"></el-input>
      </el-form-item>
      <el-form-item label="帖子图" prop="imageList">
        <img v-for="image in dataForm.imageList" :key="image" :src="image" width="80" height="80">
      </el-form-item>
      <el-form-item label="内容" prop="content">
        <el-input
          type="textarea"
          :autosize="{ minRows: 4, maxRows: 8}"
          placeholder="内容"
          v-model="dataForm.content">
        </el-input>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">关闭</el-button>
    </span>
  </el-dialog>
</template>

<script>
  export default {
    data () {
      return {
        visible: false,
        dataForm: {
          id: 0,
          week: '',
          nickName: '',
          temperature: '',
          weather: '',
          imageList: '',
          content: '',
          linkUrl: ''
        }
      }
    },
    methods: {
      init (id) {
        this.dataForm.id = id || 0
        this.visible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].resetFields()
          if (this.dataForm.id) {
            this.$http({
              url: this.$http.adornUrl(`/post/post/info/${this.dataForm.id}`),
              method: 'get',
              params: this.$http.adornParams()
            }).then(({data}) => {
              if (data && data.code === 0) {
                this.dataForm.week = data.post.week
                this.dataForm.nickName = data.post.nickName
                this.dataForm.temperature = data.post.temperature
                this.dataForm.weather = data.post.weather
                this.dataForm.content = data.post.content
                this.dataForm.linkUrl = data.post.linkUrl
                this.dataForm.imageList = data.post.imageList
              }
            })
          }
        })
      }
    }
  }
</script>
