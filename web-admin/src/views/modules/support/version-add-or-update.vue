<template>
  <el-dialog
    :title="!dataForm.id ? '新增' : '修改'"
    :close-on-click-modal="false"
    :visible.sync="visible">
    <el-form :model="dataForm" :rules="dataRule" ref="dataForm" @keyup.enter.native="dataFormSubmit()" label-width="80px">
      <el-form-item label="版本名称" prop="newVersionName">
        <el-input v-model="dataForm.newVersionName" placeholder="新版本名称"></el-input>
      </el-form-item>
      <el-form-item label="版本号" prop="newVersion">
        <el-input v-model="dataForm.newVersion" placeholder="最新版本号"></el-input>
      </el-form-item>
      <el-form-item label="文件大小" prop="size" v-if="false">
        <el-input v-model="dataForm.size" placeholder="文件大小"></el-input>
      </el-form-item>
      <el-form-item label="最小支持版本号" prop="minVersion">
        <el-input v-model="dataForm.minVersion" placeholder="最小支持版本号"></el-input>
      </el-form-item>
      <el-form-item label="描述" prop="desc">
        <el-input
          type="textarea"
          :autosize="{ minRows: 4, maxRows: 8}"
          placeholder="请输入内容"
          v-model="dataForm.desc">
        </el-input>
      </el-form-item>
      <el-form-item label="下载地址" prop="url">
        <el-input v-model="dataForm.url" placeholder="下载地址"></el-input>
      </el-form-item>
      <el-upload
        class="upload-demo"
        drag
        style="text-align: center;"
        :action="uploadUrl"
        :on-success="successHandle">
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
      </el-upload>
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
        uploadUrl: '',
        visible: false,
        dataForm: {
          id: 0,
          newVersionName: '',
          newVersion: '',
          minVersion: '',
          url: '',
          desc: '',
          size: ''
        },
        dataRule: {
          newVersionName: [
            { required: true, message: '新版本名称不能为空', trigger: 'blur' }
          ],
          newVersion: [
            { required: true, message: '最新版本号不能为空', trigger: 'blur' }
          ],
          minVersion: [
            { required: true, message: '最小支持版本号不能为空', trigger: 'blur' }
          ],
          url: [
            { required: true, message: '下载地址不能为空', trigger: 'blur' }
          ],
          desc: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ],
          size: [
            { required: true, message: '不能为空', trigger: 'blur' }
          ]
        }
      }
    },
    methods: {
      init (id) {
        this.uploadUrl = this.$http.adornUrl(`/sys/oss/upload?token=${this.$cookie.get('token')}`)
        this.dataForm.id = id || 0
        this.visible = true
        this.$nextTick(() => {
          this.$refs['dataForm'].resetFields()
          if (this.dataForm.id) {
            this.$http({
              url: this.$http.adornUrl(`/support/version/info/${this.dataForm.id}`),
              method: 'get',
              params: this.$http.adornParams()
            }).then(({data}) => {
              if (data && data.code === 0) {
                this.dataForm.newVersionName = data.appVersion.newVersionName
                this.dataForm.newVersion = data.appVersion.newVersion
                this.dataForm.minVersion = data.appVersion.minVersion
                this.dataForm.url = data.appVersion.url
                this.dataForm.desc = data.appVersion.desc
                this.dataForm.size = data.appVersion.size
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
              url: this.$http.adornUrl(`/support/version/${!this.dataForm.id ? 'save' : 'update'}`),
              method: 'post',
              data: this.$http.adornData({
                'id': this.dataForm.id || undefined,
                'newVersionName': this.dataForm.newVersionName,
                'newVersion': this.dataForm.newVersion,
                'minVersion': this.dataForm.minVersion,
                'url': this.dataForm.url,
                'desc': this.dataForm.desc,
                'size': this.dataForm.size
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
      },
      // 上传成功
      successHandle (response, file) {
        if (response && response.code === 0) {
          this.dataForm.url = response.url
          this.dataForm.size = response.fsize
        } else {
          this.$message.error(response.msg)
        }
      }
    }
  }
</script>
