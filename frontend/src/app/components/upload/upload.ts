import { HttpClient } from '@angular/common/http';
import { Component, inject } from '@angular/core';

@Component({
  selector: 'app-upload',
  imports: [],
  templateUrl: './upload.html',
  styleUrl: './upload.scss',
})
export class Upload {
  selectedFiles: File[] = [];
  currentFolderUuid: string = "83a96145-8eec-4175-8372-c8f68be9f683";

  private http = inject(HttpClient);

  onFolderSelected(event: any) {
    this.selectedFiles = Array.from(event.target.files);
  }

  uploadFolder() {
    const formData = new FormData();
    console.log(this.selectedFiles)

    formData.append('parentId', this.currentFolderUuid);

    this.selectedFiles.forEach((file: any) => {
      formData.append('files', file, file.webkitRelativePath);
    });

    this.http.post('/api/files/upload', formData).subscribe({
      next: (response: any) => {
        console.log('Upload started, Task ID:', response.taskId);
        // Start polling TaskSummary endpoint with this ID
      },
      error: (err: any) => console.error('Upload failed', err)
    });
  }
}
