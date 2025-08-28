pytest_plugins = ["pytest_playwright"]

import pytest
from playwright.sync_api import expect

LIST_URL = "http://localhost:3000/class"
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

def test_class_list_render_teacher(playwright):
    browser, context, page = login_and_get_page(playwright, TEACHER_CREDENTIALS, LIST_URL)
    expect(page.locator(".home-container")).to_be_visible(timeout=10000)
    expect(page.locator("h2")).to_have_text("我的班级")
    expect(page.locator(".class-grid")).to_be_visible()
    expect(page.locator("button.btn-primary:has-text('创建班级')")).to_be_visible()
    context.close()
    browser.close()

def test_class_list_render_student(playwright):
    browser, context, page = login_and_get_page(playwright, STUDENT_CREDENTIALS, LIST_URL)
    expect(page.locator(".home-container")).to_be_visible(timeout=10000)
    expect(page.locator("h2")).to_have_text("我的班级")
    expect(page.locator(".class-grid")).to_be_visible()
    expect(page.locator("button.btn-primary:has-text('加入班级')")).to_be_visible()
    context.close()
    browser.close()

def test_create_class_dialog_validation(playwright):
    browser, context, page = login_and_get_page(playwright, TEACHER_CREDENTIALS, LIST_URL)
    page.locator("button.btn-primary:has-text('创建班级')").click()
    expect(page.locator(".create-class-dialog")).to_be_visible()
    # 不填任何内容直接点击确定
    page.locator(".create-class-dialog .btn-primary:has-text('确定')").click()
    expect(page.locator(".create-class-dialog .error-message")).to_be_visible()
    expect(page.locator(".create-class-dialog .error-message")).to_contain_text("请选择课程")
    # 关闭弹窗
    page.locator(".create-class-dialog .btn-secondary:has-text('取消')").click()
    expect(page.locator(".create-class-dialog")).not_to_be_visible(timeout=3000)
    context.close()
    browser.close()

def test_join_class_dialog_validation(playwright):
    browser, context, page = login_and_get_page(playwright, STUDENT_CREDENTIALS, LIST_URL)
    page.locator("button.btn-primary:has-text('加入班级')").click()
    expect(page.locator(".create-class-dialog")).to_be_visible()
    # 不填内容直接点击确定
    page.locator(".create-class-dialog .btn-primary:has-text('确定')").click()
    expect(page.locator(".create-class-dialog .error-message")).to_be_visible()
    expect(page.locator(".create-class-dialog .error-message")).to_contain_text("请输入班级代码")
    # 关闭弹窗
    page.locator(".create-class-dialog .btn-secondary:has-text('取消')").click()
    expect(page.locator(".create-class-dialog")).not_to_be_visible(timeout=3000)
    context.close()
    browser.close()

