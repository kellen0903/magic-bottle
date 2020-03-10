<template>
  <el-dialog
    :title="!dataForm.id ? '新增' : '修改'"
    :close-on-click-modal="false"
    :visible.sync="visible">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="80px">
      <el-form-item label="用户昵称" prop="nickName">
        <el-input v-model="dataForm.nickName" placeholder="用户昵称"></el-input>
      </el-form-item>
      <el-form-item label="性别" size="mini" prop="sex">
        <el-radio-group v-model="dataForm.sex">
          <el-radio :label="1">男</el-radio>
          <el-radio :label="2">女</el-radio>
          <el-radio :label="3">未知</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="手机名称" prop="mobileName">
        <el-input v-model="dataForm.mobileName" placeholder="手机名称"></el-input>
      </el-form-item>
      <el-form-item label="年龄描述" prop="age">
        <el-input v-model="dataForm.age" placeholder="年龄描述"></el-input>
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
          nickName: '',
          sex: '',
          mobileName: '',
          age: '',
          deleted: '',
          status: ''
        },
        dataRule: {
          nickName: [
            { required: true, message: '用户昵称不能为空', trigger: 'blur' }
          ],
          sex: [
            { required: true, message: '性别 1：男 2：女 3：未知不能为空', trigger: 'blur' }
          ],
          mobileName: [
            { required: true, message: '手机名称不能为空', trigger: 'blur' }
          ],
          age: [
            { required: true, message: '年龄描述不能为空', trigger: 'blur' }
          ]
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
              url: this.$http.adornUrl(`/user/front-user/info/${this.dataForm.id}`),
              method: 'get',
              params: this.$http.adornParams()
            }).then(({data}) => {
              if (data && data.code === 0) {
                this.dataForm.nickName = data.magicUser.nickName
                this.dataForm.sex = data.magicUser.sex
                this.dataForm.mobileName = data.magicUser.mobileName
                this.dataForm.age = data.magicUser.age
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
              url: this.$http.adornUrl(`/user/front-user/${!this.dataForm.id ? 'save' : 'update'}`),
              method: 'post',
              data: this.$http.adornData({
                'id': this.dataForm.id || undefined,
                'nickName': this.dataForm.nickName,
                'sex': this.dataForm.sex,
                'mobileName': this.dataForm.mobileName,
                'age': this.dataForm.age,
                'deleted': this.dataForm.deleted,
                'status': this.dataForm.status
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
