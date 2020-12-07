/*
 * MIT License
 *
 * Copyright (c) 2018 Soojeong Shin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.example.newsapp;

import android.util.Log;

public class News {

    /** Title of the article */
    private String title;

    /** Section name of the article*/
    private String section;


    /** Web publication date of the article */
    private String date;

    private String image;
    private String id;
    private String webPublicationDate;
    private String webUrl;
    public News(String title, String section, String date, String image,String id,String webPublicationDate,String webUrl) {
        this.title=title;
        this.section=section;
        this.date=date;
        this.image=image;
        this.id=id;
        this.webPublicationDate=webPublicationDate;
        this.webUrl=webUrl;
    }

    /**
     * Returns the title of the article
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Returns the section name of the article.
     */
    public String getSectionName() {
        return this.section;
    }

    public String getDate() {
        return this.date;
    }

    public String getImage() {
        Log.d("News.java",this.image);
        return this.image;
    }

    public String getId() { return this.id;}
    public String getWebPublicationDate() { return this.webPublicationDate;}
    public String getWebUrl() { return this.webUrl;}
}
