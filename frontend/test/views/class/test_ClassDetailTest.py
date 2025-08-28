pytest_plugins = ["pytest_playwright"]

import pytest
import re
from playwright.sync_api import expect

BASE_URL = "http://localhost:3000/class/2"
LOGIN_URL = "http://localhost:3000/login"
TEACHER_CREDENTIALS = {"userId": "T002", "password": "123456"}
STUDENT_CREDENTIALS = {"userId": "S003", "password": "123456"}

def login_and_get_page(playwright, credentials: dict, goto_url=None):
    browser = playwright.chromium.launch()
    context = browser.new_context()
    page = context.new_page()
    page.goto(LOGIN_URL)
    page.locator("input#username").fill(credentials["userId"])
    page.locator("input#password").fill(credentials["password"])
    page.locator("button[type='submit']").click()
    expect(page).not_to_have_url(LOGIN_URL, timeout=10000)
    if goto_url:
        page.goto(goto_url)
        expect(page).to_have_url(goto_url)
    return browser, context, page

def test_class_detail_render_teacher(playwright):
    browser, context, page = login_and_get_page(playwright, TEACHER_CREDENTIALS, BASE_URL)
    expect(page.locator(".course-detail-container")).to_be_visible(timeout=10000)
    expect(page.locator(".course-header h1")).to_be_visible()
    expect(page.locator(".class-info")).to_contain_text("班级代码")
    expect(page.locator(".class-info")).to_contain_text("课程名称")
    expect(page.locator(".class-info")).to_contain_text("教师")
    # tab切换
    page.locator("button:has-text('班级成员管理')").click()
    expect(page.locator(".course-main-content")).to_be_visible()
    page.locator("button:has-text('课程作业')").click()
    expect(page.locator(".course-main-content")).to_be_visible()
    page.locator("button:has-text('学习进度')").click()
    expect(page.locator(".course-main-content")).to_be_visible()
    context.close()
    browser.close()

def test_class_detail_render_student(playwright):
    browser, context, page = login_and_get_page(playwright, STUDENT_CREDENTIALS, BASE_URL)
    expect(page.locator(".course-detail-container")).to_be_visible(timeout=10000)
    expect(page.locator(".course-header h1")).to_be_visible()
    expect(page.locator(".class-info")).to_contain_text("班级代码")
    expect(page.locator(".class-info")).to_contain_text("课程名称")
    expect(page.locator(".class-info")).to_contain_text("教师")
    # tab切换
    page.locator("button:has-text('班级成员管理')").click()
    expect(page.locator(".course-main-content")).to_be_visible()
    page.locator("button:has-text('课程作业')").click()
    expect(page.locator(".course-main-content")).to_be_visible()
    page.locator("button:has-text('学习进度')").click()
    expect(page.locator(".course-main-content")).to_be_visible()
    context.close()
    browser.close()

