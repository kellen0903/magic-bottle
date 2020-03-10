<template>
  <el-dialog
    :title="!dataForm.id ? '新增' : '修改'"
    :close-on-click-modal="false"
    :visible.sync="visible">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="80px">
      <el-form-item label="标签名" prop="tagName">
        <el-input v-model="dataForm.tagName" placeholder=""></el-input>
      </el-form-item>
      <el-form-item label="排序" prop="orderNum">
        <el-input v-model="dataForm.orderNum" placeholder="排序"></el-input>
      </el-form-item>
      <el-form-item label="ios标签描述" prop="tagIosDesc">
        <el-input
          type="textarea"
          :autosize="{ minRows: 4, maxRows: 8}"
          placeholder="请输入内容"
          v-model="dataForm.tagIosDesc">
        </el-input>
      </el-form-item>
      <el-form-item label="android标签描述" prop="tagAndroidDesc">
        <el-input
          type="textarea"
          :autosize="{ minRows: 4, maxRows: 8}"
          placeholder="请输入内容"
          v-model="dataForm.tagAndroidDesc">
        </el-input>
      </el-form-item>
      <el-form-item label="允许图片" size="mini" prop="imgFlag">
        <el-radio-group v-model="dataForm.imgFlag">
          <el-radio :label="0">是</el-radio>
          <el-radio :label="1">否</el-radio>
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
          orderNum: '',
          tagName: '',
          // tagDesc: '',
          tagIosDesc: '',
          tagAndroidDesc: '',
          deleted: 0,
          imgFlag: ''
        },
        dataRule: {
          orderNum: [
            { required: true, message: '排序不能为空', trigger: 'blur' }
          ],
          tagName: [
            { required: true, message: '标签名不能为空', trigger: 'blur' }
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
              url: this.$http.adornUrl(`/post/tag/info/${this.dataForm.id}`),
              method: 'get',
              params: this.$http.adornParams()
            }).then(({data}) => {
              if (data && data.code === 0) {
                this.dataForm.orderNum = data.tag.orderNum
                this.dataForm.tagName = data.tag.tagName
                this.dataForm.createTime = data.tag.createTime
                this.dataForm.deleted = data.tag.deleted
                this.dataForm.tagIosDesc = data.tag.tagIosDesc
                this.dataForm.tagAndroidDesc = data.tag.tagAndroidDesc
                this.dataForm.imgFlag = data.tag.imgFlag
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
              url: this.$http.adornUrl(`/post/tag/${!this.dataForm.id ? 'save' : 'update'}`),
              method: 'post',
              data: this.$http.adornData({
                'id': this.dataForm.id || undefined,
                'orderNum': this.dataForm.orderNum,
                'tagName': this.dataForm.tagName,
                'createTime': this.dataForm.createTime,
                'updateTime': this.dataForm.updateTime,
                'deleted': this.dataForm.deleted,
                'tagIosDesc': this.dataForm.tagIosDesc,
                'tagAndroidDesc': this.dataForm.tagAndroidDesc,
                'imgFlag': this.dataForm.imgFlag
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
