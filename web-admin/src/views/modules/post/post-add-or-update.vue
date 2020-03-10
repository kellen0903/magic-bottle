<template>
  <el-dialog
    :title="!dataForm.id ? '新增' : '修改'"
    :close-on-click-modal="false"
    :visible.sync="visible">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="80px">
      <el-form-item label="标签" prop="tagId">
        <el-select v-model="dataForm.tagId" clearable placeholder="请选择" :change="selectTagId">
          <el-option
            v-for="item in options"
            :key="item.id"
            :label="item.tagName"
            :value="item.id">
          </el-option>
        </el-select>
      </el-form-item>
      <!-- <el-form-item label="图片" prop="images">
        <el-input v-model="dataForm.images" placeholder="图片，多张逗号隔开"></el-input>
      </el-form-item> -->
      <el-form-item label="内容" prop="content">
        <el-input v-model="dataForm.content" placeholder="内容"></el-input>
      </el-form-item>
      <el-form-item label="用户昵称" prop="nickName">
        <el-input v-model="dataForm.nickName" placeholder="用户昵称"></el-input>
      </el-form-item>
      <el-form-item label="跳转链接" prop="linkUrl">
        <el-input v-model="dataForm.linkUrl" placeholder="广告链接"></el-input>
      </el-form-item>
      <el-form-item label="发布状态" size="mini" prop="publishStatus">
        <el-radio-group v-model="dataForm.publishStatus">
          <el-radio :label="0">不发布</el-radio>
          <el-radio :label="1">发布</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="终端" size="mini" prop="osType">
        <el-radio-group v-model="dataForm.osType">
          <el-radio :label="1">ios</el-radio>
          <el-radio :label="2">android</el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="dataFormSubmit()">确定</el-button>
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
          publishStatus: 1,
          tagId: '',
          tagName: '',
          images: '',
          content: '',
          linkUrl: '',
          osType: 1,
          nickName: ''
        },
        dataRule: {
          publishStatus: [
            { required: true, message: '发布状态 0：未发布 1：已发布不能为空', trigger: 'blur' }
          ],
          tagId: [
            { required: true, message: '标签不能为空', trigger: 'blur' }
          ],
          nickName: [
            { required: true, message: '用户昵称不能为空', trigger: 'blur' }
          ]
        },
        options: [],
        value4: ''
      }
    },
    created () {
      this.getQuerList()
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
                this.dataForm.publishStatus = data.post.publishStatus
                this.dataForm.osType = data.post.osType
                this.dataForm.tagId = data.post.tagId
                this.dataForm.tagName = data.post.tagName
                this.dataForm.images = data.post.images
                this.dataForm.content = data.post.content
                this.dataForm.linkUrl = data.post.linkUrl
                this.dataForm.nickName = data.post.nickName
              }
            })
          }
        })
      },
      getQuerList () {
        this.$http({
          url: this.$http.adornUrl('/post/tag/querList'),
          method: 'get'
        }).then(data => {
          this.options = data.data.list
        })
      },
      selectTagId (item) {
        console.log(item)
      },
      // 表单提交
      dataFormSubmit () {
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            this.$http({
              url: this.$http.adornUrl(`/post/post/${!this.dataForm.id ? 'save' : 'update'}`),
              method: 'post',
              data: this.$http.adornData({
                'id': this.dataForm.id || undefined,
                'publishStatus': this.dataForm.publishStatus,
                'tagId': this.dataForm.tagId,
                'images': this.dataForm.images,
                'content': this.dataForm.content,
                'linkUrl': this.dataForm.linkUrl,
                'osType': this.dataForm.osType,
                'nickName': this.dataForm.nickName
              })
            }).then(({data}) => {
              if (data && data.code === 0) {
                this.$message({
                  message: '操作成功',
                  type: 'success',
                  duration: 1500,
                  onClose: () => {
                    this.visible = false
                    this.$emit('refreshDataList')
                  }
                })
              } else {
                this.$message.error(data.msg)
              }
            })
          }
        })
      }
    }
  }
</script>
