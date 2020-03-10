<template>
  <el-dialog
    :title="!dataForm.id ? '新增' : '修改'"
    :visible.sync="modifyVisible">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="80px">
    <el-form-item label="话题名称" prop="topicName">
      <el-input v-model="dataForm.topicName" placeholder="话题名称"></el-input>
    </el-form-item>
    <el-form-item label="排序" prop="orderNum">
      <el-input v-model="dataForm.orderNum" placeholder="排序"></el-input>
    </el-form-item>
    </el-form>
    <span slot="footer" class="dialog-footer">
      <el-button @click="modifyVisible = false">取消</el-button>
      <el-button type="primary" @click="dataFormSubmit()">确定</el-button>
    </span>
  </el-dialog>
</template>

<script>
  export default {
    data () {
      return {
        modifyVisible: true,
        dataForm: {
          id: 0,
          topicName: '',
          tagId: '',
          orderNum: ''
        },
        dataRule: {
          topicName: [
            { required: true, message: '话题名称不能为空', trigger: 'blur' }
          ],
          orderNum: [
            { required: true, message: '排序不能为空', trigger: 'blur' }
          ]
        }
      }
    },
    methods: {
      init (id, tagId) {
        this.dataForm.id = id
        this.dataForm.tagId = tagId
        this.modifyVisible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].resetFields()
          if (this.dataForm.id) {
            this.$http({
              url: this.$http.adornUrl(`/17chf/topic/info/${this.dataForm.id}`),
              method: 'get',
              params: this.$http.adornParams()
            }).then(({data}) => {
              if (data && data.code === 0) {
                this.dataForm.topicName = data.topic.topicName
                this.dataForm.tagId = data.topic.tagId
                this.dataForm.id = data.topic.id
                this.dataForm.orderNum = data.topic.orderNum
              }
            })
          }
        })
      },
      // 表单提交
      dataFormSubmit () {
        this.$refs['dataForm'].validate((valid) => {
          if (valid) {
            this.$http({
              url: this.$http.adornUrl(`/17chf/topic/${!this.dataForm.id ? 'save' : 'update'}`),
              method: 'post',
              data: this.$http.adornData({
                'id': this.dataForm.id,
                'tagId': this.dataForm.tagId,
                'topicName': this.dataForm.topicName,
                'orderNum': this.dataForm.orderNum
              })
            }).then(({data}) => {
              if (data && data.code === 0) {
                this.$message({
                  message: '操作成功',
                  type: 'success',
                  duration: 1500,
                  onClose: () => {
                    this.modifyVisible = false
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
